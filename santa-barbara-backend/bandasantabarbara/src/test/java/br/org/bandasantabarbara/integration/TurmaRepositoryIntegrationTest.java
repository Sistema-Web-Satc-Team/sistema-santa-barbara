package br.org.bandasantabarbara.integration;


import br.org.bandasantabarbara.model.Horario;
import br.org.bandasantabarbara.model.Sala;
import br.org.bandasantabarbara.model.Turma;
import br.org.bandasantabarbara.repositories.StatusTurma;
import br.org.bandasantabarbara.repositories.TurmaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TurmaRepositoryIntegrationTest {

    @Autowired
    private TurmaRepository turmaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void deveCarregarOStatusCorretoPelaFormulaDoBanco() {
        Sala sala = new Sala("1.10", 10);
        entityManager.persist(sala);

        Turma turmaPlanejada = new Turma("Turma Planejamento", sala);
        turmaPlanejada.agendarPeriodo(LocalDate.now().plusDays(5), null);
        entityManager.persist(turmaPlanejada);

        Turma turmaEmAndamento = new Turma("Turma Andamento", sala);
        turmaEmAndamento.agendarPeriodo(LocalDate.now(), LocalDate.now().plusDays(5));
        entityManager.persist(turmaEmAndamento);

        entityManager.flush();
        entityManager.clear();

        Turma buscadaPlanejada = turmaRepository.findById(turmaPlanejada.getId()).orElseThrow();
        Turma buscadaAndamento = turmaRepository.findById(turmaEmAndamento.getId()).orElseThrow();

        Assertions.assertThat(buscadaPlanejada.getStatus()).isEqualTo(StatusTurma.PLANEJAMENTO);
        Assertions.assertThat(buscadaAndamento.getStatus()).isEqualTo(StatusTurma.EM_ANDAMENTO);
    }

    @Test
    public void deveFiltrarPorStatusEmAndamento() {
        Sala sala = new Sala("1.10", 10);
        entityManager.persist(sala);

        Turma turmaPlanejada = new Turma("Turma Planejamento", sala);
        turmaPlanejada.agendarPeriodo(LocalDate.now().plusDays(5), null);
        entityManager.persist(turmaPlanejada);

        Turma turmaEmAndamento = new Turma("Turma Andamento", sala);
        turmaEmAndamento.agendarPeriodo(LocalDate.now(), LocalDate.now().plusDays(5));
        entityManager.persist(turmaEmAndamento);

        entityManager.flush();
        entityManager.clear();


        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by("nome").ascending()
        );

        Page<Turma> turmas = this.turmaRepository.findAllWithHorarioSalaAndOptionalFilters(StatusTurma.EM_ANDAMENTO, pageable);

        turmas.stream().forEach((turma) -> {
            assertEquals(StatusTurma.EM_ANDAMENTO, turma.getStatus());
        });

    }

    @Test
    public void deveListarAsTurmasComSeusHorariosESala() {
        Sala sala = new Sala("1.10", 10);
        entityManager.persist(sala);

        Turma turmaPlanejada = new Turma("Turma Planejamento", sala);
        turmaPlanejada.agendarPeriodo(LocalDate.now().plusDays(5), null);

        var horarioTurmaPlanejada = new Horario();
        horarioTurmaPlanejada.setSegunda(LocalTime.of(19,0),LocalTime.of(20,0));

        entityManager.persist(turmaPlanejada);

        Turma turmaEmAndamento = new Turma("Turma Andamento", sala);
        turmaEmAndamento.agendarPeriodo(LocalDate.now(), LocalDate.now().plusDays(14));

        var horarioTurmaEmAndamento = new Horario();
        horarioTurmaEmAndamento.setTerca(LocalTime.of(19,0),LocalTime.of(20,0));

        entityManager.persist(turmaEmAndamento);

        entityManager.flush();
        entityManager.clear();


        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by("nome").ascending()
        );

        Page<Turma> turmas = this.turmaRepository.findAllWithHorarioSalaAndOptionalFilters(null, pageable);

        turmas.stream().forEach((turma) -> {

            assertThat(turma.getSala())
                    .usingRecursiveComparison()
                    .isEqualTo(sala);


            if (turma.getStatus() == StatusTurma.PLANEJAMENTO) {
                assertThat(turma.getHorario())
                        .usingRecursiveComparison()
                        .isEqualTo(horarioTurmaPlanejada);
            }

            if (turma.getStatus() == StatusTurma.EM_ANDAMENTO) {
                assertThat(turma.getHorario())
                        .usingRecursiveComparison()
                        .isEqualTo(horarioTurmaEmAndamento);
            }
        });

    }

}
