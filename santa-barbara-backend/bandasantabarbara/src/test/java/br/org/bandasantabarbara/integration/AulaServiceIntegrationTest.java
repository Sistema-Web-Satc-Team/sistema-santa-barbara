package br.org.bandasantabarbara.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.org.bandasantabarbara.application.dtos.aulas.AtualizarChamadasRequest;
import br.org.bandasantabarbara.application.dtos.aulas.ChamadaRequest;
import br.org.bandasantabarbara.application.services.aula.AulaService;
import br.org.bandasantabarbara.application.services.aula.AulaLifecycleScheduler;
import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.exception.ForbiddenException;
import br.org.bandasantabarbara.model.aula.Aula;
import br.org.bandasantabarbara.model.aula.Chamada;
import br.org.bandasantabarbara.model.ensino.Aluno;
import br.org.bandasantabarbara.model.ensino.Professor;
import br.org.bandasantabarbara.model.membro.Membro;
import br.org.bandasantabarbara.model.turma.Horario;
import br.org.bandasantabarbara.model.turma.MatriculaAlunoTurma;
import br.org.bandasantabarbara.model.turma.ProfessorTurma;
import br.org.bandasantabarbara.model.turma.Sala;
import br.org.bandasantabarbara.model.turma.Turma;
import jakarta.persistence.EntityManager;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AulaServiceIntegrationTest {

    private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");

    @Autowired
    private AulaService aulaService;

    @Autowired
    private AulaLifecycleScheduler lifecycleScheduler;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void iniciaAulaDentroDoHorarioPermitido() {
        Fixture fixture = criarFixture(true, true, 1, false);

        var response = aulaService.iniciar(fixture.turma().getId(), fixture.membroProfessorId());

        entityManager.flush();
        entityManager.clear();
        Aula aula = entityManager.find(Aula.class, response.id());
        assertThat(aula.getStatusAula()).isEqualTo(Aula.EnumStatusAula.EM_ANDAMENTO);
        assertThat(aula.getTurma().getId()).isEqualTo(fixture.turma().getId());
        assertThat(aula.getProfessor().getId()).isEqualTo(fixture.professor().getId());
        assertThat(aula.getDataHoraFimPrevista()).isAfter(aula.getDataHoraInicio());
    }

    @Test
    void rejeitaInicioForaDoHorarioDaTurma() {
        Fixture fixture = criarFixture(true, false, 1, false);

        assertThatThrownBy(() -> aulaService.iniciar(fixture.turma().getId(), fixture.membroProfessorId()))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("O horário atual não pertence a um horário da turma.");
    }

    @Test
    void rejeitaInicioQuandoTurmaAindaEstaEmPlanejamento() {
        Fixture fixture = criarFixture(false, true, 1, false);

        assertThatThrownBy(() -> aulaService.iniciar(fixture.turma().getId(), fixture.membroProfessorId()))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Só é possível iniciar uma aula de uma turma em andamento.");
    }

    @Test
    void finalizaAulaManualmente() {
        Fixture fixture = criarFixture(true, true, 1, false);
        var iniciada = aulaService.iniciar(fixture.turma().getId(), fixture.membroProfessorId());

        var finalizada = aulaService.finalizar(
            fixture.turma().getId(),
            iniciada.id(),
            fixture.membroProfessorId()
        );

        assertThat(finalizada.status()).isEqualTo(Aula.EnumStatusAula.FINALIZADA);
        assertThat(finalizada.tipoEncerramento()).isEqualTo(Aula.EnumTipoEncerramento.MANUAL);
        assertThat(finalizada.dataHoraFim()).isNotNull();
    }

    @Test
    void rejeitaFinalizacaoDupla() {
        Fixture fixture = criarFixture(true, true, 1, false);
        var aula = aulaService.iniciar(fixture.turma().getId(), fixture.membroProfessorId());
        aulaService.finalizar(fixture.turma().getId(), aula.id(), fixture.membroProfessorId());

        assertThatThrownBy(() -> aulaService.finalizar(
            fixture.turma().getId(),
            aula.id(),
            fixture.membroProfessorId()
        ))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("A aula já foi finalizada.");
    }

    @Test
    void reabreAulaFinalizadaManualmenteAntesDoFimPrevisto() {
        Fixture fixture = criarFixture(true, true, 1, false);
        var aula = aulaService.iniciar(fixture.turma().getId(), fixture.membroProfessorId());
        aulaService.finalizar(fixture.turma().getId(), aula.id(), fixture.membroProfessorId());

        var reaberta = aulaService.reabrir(fixture.turma().getId(), aula.id(), fixture.membroProfessorId());

        assertThat(reaberta.status()).isEqualTo(Aula.EnumStatusAula.EM_ANDAMENTO);
        assertThat(reaberta.dataHoraFim()).isNull();
        assertThat(reaberta.tipoEncerramento()).isNull();
    }

    @Test
    void rejeitaReaberturaDepoisDoHorarioPrevisto() {
        Fixture fixture = criarFixture(true, true, 1, false);
        var aula = aulaService.iniciar(fixture.turma().getId(), fixture.membroProfessorId());
        aulaService.finalizar(fixture.turma().getId(), aula.id(), fixture.membroProfessorId());
        definirFimPrevistoComoExpirado(aula.id());

        assertThatThrownBy(() -> aulaService.reabrir(
            fixture.turma().getId(),
            aula.id(),
            fixture.membroProfessorId()
        ))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("O horário previsto para a aula já foi ultrapassado.");
    }

    @Test
    void rejeitaReaberturaDeAulaEncerradaAutomaticamente() {
        Fixture fixture = criarFixture(true, true, 1, false);
        var aula = aulaService.iniciar(fixture.turma().getId(), fixture.membroProfessorId());
        definirFimPrevistoComoExpirado(aula.id());
        lifecycleScheduler.encerrarAulasVencidas();
        entityManager.clear();

        assertThatThrownBy(() -> aulaService.reabrir(
            fixture.turma().getId(),
            aula.id(),
            fixture.membroProfessorId()
        ))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Apenas aulas finalizadas manualmente podem ser reabertas.");
    }

    @Test
    void registraEAtualizaChamadasDosAlunosAtivos() {
        Fixture fixture = criarFixture(true, true, 2, false);
        var aula = aulaService.iniciar(fixture.turma().getId(), fixture.membroProfessorId());
        List<ChamadaRequest> chamadas = fixture.matriculas().stream()
            .map(matricula -> new ChamadaRequest(matricula.getId(), true))
            .toList();

        var registradas = aulaService.atualizarChamadas(
            fixture.turma().getId(),
            aula.id(),
            fixture.membroProfessorId(),
            new AtualizarChamadasRequest(chamadas)
        );
        var atualizadas = aulaService.atualizarChamadas(
            fixture.turma().getId(),
            aula.id(),
            fixture.membroProfessorId(),
            new AtualizarChamadasRequest(List.of(
                new ChamadaRequest(fixture.matriculas().get(0).getId(), false),
                new ChamadaRequest(fixture.matriculas().get(1).getId(), true)
            ))
        );

        assertThat(registradas).hasSize(2).allSatisfy(chamada -> assertThat(chamada.presente()).isTrue());
        assertThat(atualizadas)
            .extracting(chamada -> chamada.presente())
            .containsExactly(false, true);
        assertThat(entityManager.createQuery(
            "SELECT chamada FROM Chamada chamada WHERE chamada.aula.id = :aulaId ORDER BY chamada.matriculaAlunoTurma.id",
            Chamada.class
        ).setParameter("aulaId", aula.id()).getResultList())
            .extracting(Chamada::isPresente)
            .containsExactly(false, true);
    }

    @Test
    void impedeAlteracaoDeChamadasDepoisDoEncerramento() {
        Fixture fixture = criarFixture(true, true, 1, false);
        var aula = aulaService.iniciar(fixture.turma().getId(), fixture.membroProfessorId());
        aulaService.finalizar(fixture.turma().getId(), aula.id(), fixture.membroProfessorId());

        assertThatThrownBy(() -> aulaService.atualizarChamadas(
            fixture.turma().getId(),
            aula.id(),
            fixture.membroProfessorId(),
            new AtualizarChamadasRequest(List.of(
                new ChamadaRequest(fixture.matriculas().get(0).getId(), true)
            ))
        ))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Não é possível alterar chamadas de uma aula finalizada.");
    }

    @Test
    void encerraAutomaticamenteAulaComTempoPrevistoExpirado() {
        Fixture fixture = criarFixture(true, true, 1, false);
        var aula = aulaService.iniciar(fixture.turma().getId(), fixture.membroProfessorId());
        definirFimPrevistoComoExpirado(aula.id());

        lifecycleScheduler.encerrarAulasVencidas();

        entityManager.clear();
        Aula encerrada = entityManager.find(Aula.class, aula.id());
        assertThat(aulaService.encerrarAulasVencidas()).isZero();
        assertThat(encerrada.getStatusAula()).isEqualTo(Aula.EnumStatusAula.FINALIZADA);
        assertThat(encerrada.getTipoEncerramento()).isEqualTo(Aula.EnumTipoEncerramento.TEMPO_ESGOTADO);
        assertThat(encerrada.getDataHoraFim()).isEqualTo(encerrada.getDataHoraFimPrevista());
    }

    @Test
    void restringeAlteracoesEConsultasAProfessoresVinculados() {
        Fixture fixture = criarFixture(true, true, 1, true);
        var aula = aulaService.iniciar(fixture.turma().getId(), fixture.membroProfessorId());
        UUID professorNaoResponsavel = fixture.outroProfessor().getId();
        UUID membroSemVinculo = UUID.randomUUID();

        assertThatThrownBy(() -> aulaService.finalizar(
            fixture.turma().getId(),
            aula.id(),
            professorNaoResponsavel
        )).isInstanceOf(ForbiddenException.class);
        assertThatThrownBy(() -> aulaService.finalizar(
            fixture.turma().getId(),
            aula.id(),
            membroSemVinculo
        )).isInstanceOf(ForbiddenException.class);
        assertThatThrownBy(() -> aulaService.buscarChamadas(
            fixture.turma().getId(),
            aula.id(),
            membroSemVinculo,
            false
        )).isInstanceOf(ForbiddenException.class);

        assertThat(aulaService.buscarChamadas(
            fixture.turma().getId(),
            aula.id(),
            membroSemVinculo,
            true
        ).aula().id()).isEqualTo(aula.id());
    }

    @Test
    void consultaAulasComPaginacaoEFiltroPorTipoDeEncerramento() {
        Fixture fixture = criarFixture(true, true, 1, false);
        var aulaManual = aulaService.iniciar(fixture.turma().getId(), fixture.membroProfessorId());
        aulaService.finalizar(fixture.turma().getId(), aulaManual.id(), fixture.membroProfessorId());
        var aulaAutomatica = aulaService.iniciar(fixture.turma().getId(), fixture.membroProfessorId());
        definirFimPrevistoComoExpirado(aulaAutomatica.id());
        lifecycleScheduler.encerrarAulasVencidas();
        entityManager.clear();

        var manuais = aulaService.listar(
            fixture.turma().getId(),
            fixture.membroProfessorId(),
            false,
            Aula.EnumTipoEncerramento.MANUAL,
            org.springframework.data.domain.PageRequest.of(0, 1)
        );
        var automaticas = aulaService.listar(
            fixture.turma().getId(),
            fixture.membroProfessorId(),
            false,
            Aula.EnumTipoEncerramento.TEMPO_ESGOTADO,
            org.springframework.data.domain.PageRequest.of(0, 1)
        );

        assertThat(manuais.aulas().content()).singleElement()
            .satisfies(item -> assertThat(item.tipoEncerramento()).isEqualTo(Aula.EnumTipoEncerramento.MANUAL));
        assertThat(automaticas.aulas().content()).singleElement()
            .satisfies(item -> assertThat(item.tipoEncerramento()).isEqualTo(Aula.EnumTipoEncerramento.TEMPO_ESGOTADO));
        assertThat(manuais.aulas().totalElements()).isEqualTo(1);
        assertThat(automaticas.aulas().totalElements()).isEqualTo(1);
    }

    private Fixture criarFixture(
        boolean emAndamento,
        boolean horarioAtual,
        int quantidadeAlunos,
        boolean criarOutroProfessor
    ) {
        String sufixo = UUID.randomUUID().toString();
        Sala sala = new Sala("sala-aula-" + sufixo, 20);
        entityManager.persist(sala);

        Turma turma = new Turma("turma-aula-" + sufixo, sala);
        turma.configurarHorario(Turma.EnumTipoTurno.CONTRATURNO, horario(horarioAtual));
        turma.agendarPeriodo(
            emAndamento ? LocalDate.now(ZONE_ID) : LocalDate.now(ZONE_ID).plusDays(1),
            LocalDate.now(ZONE_ID).plusDays(30)
        );
        entityManager.persist(turma);

        Membro membroProfessor = criarMembro("professor-aula-" + sufixo);
        Professor professor = new Professor();
        professor.setMembro(membroProfessor);
        entityManager.persist(professor);
        entityManager.persist(new ProfessorTurma(turma, professor, LocalDate.now(ZONE_ID)));

        Professor outroProfessor = null;
        if (criarOutroProfessor) {
            Membro membroOutroProfessor = criarMembro("outro-professor-aula-" + sufixo);
            outroProfessor = new Professor();
            outroProfessor.setMembro(membroOutroProfessor);
            entityManager.persist(outroProfessor);
            entityManager.persist(new ProfessorTurma(turma, outroProfessor, LocalDate.now(ZONE_ID)));
        }

        List<MatriculaAlunoTurma> matriculas = new ArrayList<>();
        for (int indice = 0; indice < quantidadeAlunos; indice++) {
            Membro membroAluno = criarMembro("aluno-aula-" + indice + "-" + sufixo);
            Aluno aluno = new Aluno();
            aluno.setMembro(membroAluno);
            entityManager.persist(aluno);
            MatriculaAlunoTurma matricula = new MatriculaAlunoTurma(turma, aluno, LocalDate.now(ZONE_ID));
            entityManager.persist(matricula);
            matriculas.add(matricula);
        }
        entityManager.flush();
        return new Fixture(turma, professor, membroProfessor.getId(), outroProfessor, matriculas);
    }

    private Horario horario(boolean incluiHoje) {
        Horario horario = new Horario();
        LocalTime agora = LocalTime.now(ZONE_ID);
        LocalTime inicio = agora.minusMinutes(20);
        LocalTime fim = agora.plusMinutes(20);
        if (inicio.isAfter(agora)) {
            inicio = LocalTime.MIN;
        }
        if (!fim.isAfter(agora)) {
            fim = LocalTime.MAX;
        }
        if (incluiHoje) {
            horario.adicionar(DayOfWeek.from(LocalDate.now(ZONE_ID)), inicio, fim);
        } else {
            horario.adicionar(
                DayOfWeek.from(LocalDate.now(ZONE_ID).plusDays(1)),
                inicio,
                fim
            );
        }
        return horario;
    }

    private Membro criarMembro(String nome) {
        Membro membro = new Membro();
        membro.setNome(nome);
        membro.setDataNascimento(LocalDate.now().minusYears(20));
        membro.setSexo(Membro.MembroSexoEnum.MASCULINO);
        membro.setEmail(nome + "-" + UUID.randomUUID() + "@example.test");
        membro.setTelefone("48000000001");
        entityManager.persist(membro);
        return membro;
    }

    private void definirFimPrevistoComoExpirado(Long aulaId) {
        entityManager.flush();
        jdbcTemplate.update(
            "UPDATE aula SET data_hora_fim_prevista = CURRENT_TIMESTAMP - INTERVAL '1 minute' WHERE id = ?",
            aulaId
        );
        entityManager.clear();
    }

    private record Fixture(
        Turma turma,
        Professor professor,
        UUID membroProfessorId,
        Professor outroProfessor,
        List<MatriculaAlunoTurma> matriculas
    ) {}
}
