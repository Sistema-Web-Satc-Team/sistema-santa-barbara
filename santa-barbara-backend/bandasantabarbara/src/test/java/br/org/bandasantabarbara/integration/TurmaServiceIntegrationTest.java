package br.org.bandasantabarbara.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.sala.SalaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.AtualizarTurmaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.CriarTurmaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.HorarioRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.TurmaResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.TurmaResponseResumidoDTO;
import br.org.bandasantabarbara.application.services.turma.SalaService;
import br.org.bandasantabarbara.application.services.turma.TurmaService;
import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.model.turma.StatusTurma;
import br.org.bandasantabarbara.model.turma.Turma;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TurmaServiceIntegrationTest {

    @Autowired
    private TurmaService turmaService;

    @Autowired
    private SalaService salaService;

    @Test
    void postCriaTurmaBasicaSemPeriodoOuHorario() {
        cadastrarSala("sala-post", 12);

        TurmaResponseDTO turma = turmaService.criar(new CriarTurmaRequestDTO(
            "sala-post",
            12,
            "aulas de clarinete",
            Turma.EnumTipoTurno.MATUTINO,
            null
        ));

        assertThat(turma.nome()).isEqualTo("aulas de clarinete");
        assertThat(turma.numeroVagas()).isEqualTo(12);
        assertThat(turma.dataInicio()).isNull();
        assertThat(turma.dataFim()).isNull();
        assertThat(turma.horarios()).isNull();
        assertThat(turma.status()).isEqualTo(StatusTurma.PLANEJAMENTO);
    }

    @Test
    void naoPermiteVagasAcimaDaCapacidadeNoPost() {
        cadastrarSala("sala-capacidade", 12);

        assertThatThrownBy(() -> turmaService.criar(new CriarTurmaRequestDTO(
            "sala-capacidade",
            13,
            "turma inválida",
            Turma.EnumTipoTurno.MATUTINO,
            null
        )))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("vagas não pode exceder a capacidade máxima da sala");
    }

    @Test
    void patchDePeriodoExigeHorarioEParticipantesAtivos() {
        cadastrarSala("sala-periodo", 12);
        TurmaResponseDTO turma = turmaService.criar(new CriarTurmaRequestDTO(
            "sala-periodo",
            12,
            "turma sem configuração",
            Turma.EnumTipoTurno.MATUTINO,
            null
        ));
        LocalDate inicio = LocalDate.now().plusDays(7);

        assertThatThrownBy(() -> turmaService.atualizar(turma.id(), new AtualizarTurmaRequestDTO(
            null,
            null,
            inicio,
            inicio.plusDays(7),
            null,
            null,
            null
        )))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("Defina ao menos um horário");
    }

    @Test
    void patchPodeConfigurarHorarioAntesDoPeriodo() {
        cadastrarSala("sala-patch-horario", 12);
        TurmaResponseDTO criada = turmaService.criar(new CriarTurmaRequestDTO(
            "sala-patch-horario",
            12,
            "turma em planejamento",
            Turma.EnumTipoTurno.MATUTINO,
            null
        ));

        TurmaResponseDTO atualizada = turmaService.atualizar(criada.id(), new AtualizarTurmaRequestDTO(
            null,
            null,
            null,
            null,
            null,
            null,
            List.of(new HorarioRequestDTO(
                DayOfWeek.MONDAY,
                LocalTime.of(10, 0),
                LocalTime.of(11, 0)
            ))
        ));

        assertThat(atualizada.horarios().segunda()).hasSize(1);
        assertThat(atualizada.dataInicio()).isNull();
        assertThat(atualizada.status()).isEqualTo(StatusTurma.PLANEJAMENTO);
    }

    @Test
    void listaTurmasPaginadas() {
        cadastrarSala("sala-pagina-a", 12);
        cadastrarSala("sala-pagina-b", 15);
        cadastrarSala("sala-pagina-c", 20);
        turmaService.criar(turma("sala-pagina-a", 12, "clarinete"));
        turmaService.criar(turma("sala-pagina-b", 15, "flauta"));
        turmaService.criar(turma("sala-pagina-c", 20, "violino"));

        PageResponse<TurmaResponseResumidoDTO> pagina = turmaService.listar(
            PageRequest.of(0, 10, Sort.by("nome").ascending())
        );

        assertThat(pagina.content()).hasSize(3);
        assertThat(pagina.totalElements()).isEqualTo(3);
        assertThat(pagina.page()).isZero();
        assertThat(pagina.totalPages()).isEqualTo(1);
    }

    private CriarTurmaRequestDTO turma(String codigoSala, int vagas, String nome) {
        return new CriarTurmaRequestDTO(
            codigoSala,
            vagas,
            nome,
            Turma.EnumTipoTurno.MATUTINO,
            null
        );
    }

    private void cadastrarSala(String codigo, int capacidade) {
        salaService.cadastrar(new SalaRequestDTO(codigo, capacidade));
    }
}
