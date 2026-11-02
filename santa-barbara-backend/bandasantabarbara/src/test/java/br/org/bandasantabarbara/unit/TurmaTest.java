package br.org.bandasantabarbara.unit;

import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.model.turma.Horario;
import br.org.bandasantabarbara.model.turma.Sala;
import br.org.bandasantabarbara.model.turma.Turma;
import br.org.bandasantabarbara.model.turma.StatusTurma;
import br.org.bandasantabarbara.model.turma.MatriculaAlunoTurma;
import br.org.bandasantabarbara.model.turma.ProfessorTurma;
import br.org.bandasantabarbara.model.ensino.Aluno;
import br.org.bandasantabarbara.model.ensino.Professor;
import br.org.bandasantabarbara.application.dtos.turmas.AtualizarTurmaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.CriarTurmaRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class TurmaTest {


    @Test
    public void DeveCriarUmaTurma() {
        var turma = new Turma("Turma - 01", new Sala("1.10", 10));

        Horario horario = new Horario();

        horario.setSegunda(LocalTime.of(19,0), LocalTime.of(22,0));

        turma.setHorario(horario);

        LocalDate dataInicio = LocalDate.now().plusDays(5);
        LocalDate dataFim = LocalDate.now().plusMonths(3);

        turma.agendarPeriodo(dataInicio, dataFim);

        Assertions.assertEquals(dataInicio, turma.getDataInicio());
        Assertions.assertEquals(dataFim, turma.getDataFim());

        Assertions.assertEquals("turma - 01", turma.getNome());
        assertThat(turma.getSala())
                .usingRecursiveComparison()
                .isEqualTo(new Sala("1.10", 10));

        assertThat(turma.getHorario())
                .usingRecursiveComparison()
                .isEqualTo(horario);
    }

    @Test
    public void naoDevePermitirDataFimSemDataInicio() {
        var turma = new Turma("turma - 01", new Sala("1.10", 10));

        Assertions.assertThrows(BadRequestException.class, () -> {
            turma.agendarPeriodo(null, LocalDate.now().plusMonths(3));
        });
    }

    @Test
    public void naoDevePermitirDataFimAnteriorADataInicio() {
        var turma = new Turma("turma - 01", new Sala("1.10", 10));

        Assertions.assertThrows(BadRequestException.class, () -> {
            turma.agendarPeriodo(LocalDate.now().plusMonths(4), LocalDate.now().plusMonths(3));
        });
    }

    @Test
    public void naoDevePermitirDataInicioNoPassado() {
        var turma = new Turma("turma - 01", new Sala("1.10", 10));

        Assertions.assertThrows(BadRequestException.class, () -> {
            turma.agendarPeriodo(LocalDate.now().minusDays(1), null);
        });
    }

    @Test
    public void deveMostrarQueTurmaEstaEmPlanejamentoQuandoDataInicioEhNulaOuEstaNoFuturo() {
        var turma = new Turma("turma - 01", new Sala("1.10", 10));
        turma.agendarPeriodo(LocalDate.now().plusDays(5), null);

        Assertions.assertEquals(StatusTurma.PLANEJAMENTO, turma.getStatus());
    }

    @Test
    public void deveMostrarQueTurmaEncerrouAutomaticamente() {
        var turma = new Turma("turma - 01", new Sala("1.10", 10));
        var dataHoje = LocalDate.now();
        turma.agendarPeriodo(dataHoje, dataHoje);

        Assertions.assertEquals(StatusTurma.ENCERRADO, turma.getStatus());


    }

    @Test
    public void deveMostrarQueTurmaEncerrouManualmente() {
        var turma = new Turma("turma - 01", new Sala("1.10", 10));
        turma.agendarPeriodo(LocalDate.now(), null);

        turma.terminar();

        Assertions.assertEquals(StatusTurma.ENCERRADO, turma.getStatus());
    }

    @Test
    public void deveMostrarQueTurmaEstaEmAndamento() {
        var turma = new Turma("turma - 01", new Sala("1.10", 10));
        turma.agendarPeriodo(LocalDate.now(), LocalDate.now().plusDays(1));

        Assertions.assertEquals(StatusTurma.EM_ANDAMENTO, turma.getStatus());

    }

    @Test
    public void deveMostrarQueTurmaCancelou() {
        var turma = new Turma("turma - 01", new Sala("1.10", 10));
        turma.agendarPeriodo(LocalDate.now().plusDays(2), null);

        turma.terminar();

        Assertions.assertEquals(StatusTurma.CANCELADO, turma.getStatus());
    }

    @Test
    public void deveMostrarQueTurmaInterrompeu() {
        var turma = new Turma("turma - 01", new Sala("1.10", 10));
        turma.agendarPeriodo(LocalDate.now(), LocalDate.now().plusDays(4));

        turma.terminar();

        Assertions.assertEquals(StatusTurma.INTERROMPIDA, turma.getStatus());
    }

    @Test
    public void deveImpedirHorarioForaDoTurno() {
        var turma = new Turma("turma - 01", new Sala("1.10", 10));
        turma.setTurno(Turma.EnumTipoTurno.MATUTINO);

        Horario horario = new Horario();
        horario.setSegunda(LocalTime.of(11, 30), LocalTime.of(12, 30));

        Assertions.assertThrows(BadRequestException.class, () -> turma.setHorario(horario));
    }

    @Test
    public void deveAceitarHorarioNoTurnoNoturno() {
        var turma = new Turma("turma - 01", new Sala("1.10", 10));
        turma.setTurno(Turma.EnumTipoTurno.NOTURNO);

        Horario horario = new Horario();
        horario.setSegunda(LocalTime.of(18, 0), LocalTime.of(20, 0));

        Assertions.assertDoesNotThrow(() -> turma.setHorario(horario));
    }

    @Test
    public void desistirDeveRegistrarStatusDesistido() {
        var turma = new Turma("turma - 01", new Sala("1.10", 10));
        Aluno aluno = new Aluno();
        MatriculaAlunoTurma matricula = new MatriculaAlunoTurma(turma, aluno, LocalDate.now());

        matricula.desistir(LocalDate.now());

        assertThat(matricula.getStatus()).isEqualTo("DESISTIDO");
    }

    @Test
    public void transferirDevePreservarStatusEJustificativa() {
        var turma = new Turma("turma - 01", new Sala("1.10", 10));
        Aluno aluno = new Aluno();
        MatriculaAlunoTurma matricula = new MatriculaAlunoTurma(turma, aluno, LocalDate.now());

        matricula.transferir(LocalDate.now(), "Solicitação do aluno");

        assertThat(matricula.getStatus()).isEqualTo("TRANSFERIDO");
        assertThat(matricula.getJustificativaMudancaStatus()).isEqualTo("Solicitação do aluno");
    }

    @Test
    public void substituirProfessorDeveLiberarVinculoAtivo() {
        var turma = new Turma("turma - 01", new Sala("1.10", 10));
        Professor professor = new Professor();
        ProfessorTurma vinculo = new ProfessorTurma(turma, professor, LocalDate.now());

        vinculo.substituir(LocalDate.now());

        assertThat(vinculo.getStatus()).isEqualTo("SUBSTITUIDO");
        assertThat(vinculo.getDataSaida()).isEqualTo(LocalDate.now());
    }

    @Test
    public void dtoIgnoraStatusEnviadoNoBodyDeCriacaoOuAtualizacao() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String corpoCriacao = """
            {
              "codigoSala": "1.10",
              "numeroVagas": 10,
              "dataInicio": "2099-01-01",
              "dataFim": "2099-02-01",
              "nome": "Turma",
              "turno": "NOTURNO",
              "horarios": [{"diaDaSemana": "MONDAY", "inicio": "19:00", "fim": "20:00"}],
              "status": "CANCELADO"
            }
            """;
        String corpoAtualizacao = """
            {"nome": "Turma atualizada", "status": "CANCELADO"}
            """;

        CriarTurmaRequestDTO criacao = mapper.readValue(corpoCriacao, CriarTurmaRequestDTO.class);
        AtualizarTurmaRequestDTO atualizacao = mapper.readValue(corpoAtualizacao, AtualizarTurmaRequestDTO.class);

        assertThat(criacao.nome()).isEqualTo("Turma");
        assertThat(atualizacao.nome()).isEqualTo("Turma atualizada");
    }
}
