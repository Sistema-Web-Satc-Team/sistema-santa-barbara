package br.org.bandasantabarbara.unit;

import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.model.Horario;
import br.org.bandasantabarbara.model.Sala;
import br.org.bandasantabarbara.model.Turma;
import br.org.bandasantabarbara.repositories.StatusTurma;
import org.junit.jupiter.api.Assertions;
import org.assertj.core.api.*;
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

        Assertions.assertEquals("Turma - 01", turma.getNome());
        assertThat(turma.getSala())
                .usingRecursiveComparison()
                .isEqualTo(new Sala("1.10", 10));

        assertThat(turma.getHorario())
                .usingRecursiveComparison()
                .isEqualTo(horario);
    }

    @Test
    public void naoDevePermitirDataFimSemDataInicio() {
        var turma = new Turma("Turma - 01", new Sala("1.10", 10));

        Assertions.assertThrows(BadRequestException.class, () -> {
            turma.agendarPeriodo(null, LocalDate.now().plusMonths(3));
        });
    }

    @Test
    public void naoDevePermitirDataFimAnteriorADataInicio() {
        var turma = new Turma("Turma - 01", new Sala("1.10", 10));

        Assertions.assertThrows(BadRequestException.class, () -> {
            turma.agendarPeriodo(LocalDate.now().plusMonths(4), LocalDate.now().plusMonths(3));
        });
    }

    @Test
    public void naoDevePermitirDataInicioNoPassado() {
        var turma = new Turma("Turma - 01", new Sala("1.10", 10));

        Assertions.assertThrows(BadRequestException.class, () -> {
            turma.agendarPeriodo(LocalDate.now().minusDays(1), null);
        });
    }

    @Test
    public void deveMostrarQueTurmaEstaEmPlanejamentoQuandoDataInicioEhNulaOuEstaNoFuturo() {
        var turma = new Turma("Turma - 01", new Sala("1.10", 10));
        turma.agendarPeriodo(LocalDate.now().plusDays(5), null);

        Assertions.assertEquals(StatusTurma.PLANEJAMENTO, turma.getStatus());
    }

    @Test
    public void deveMostrarQueTurmaEncerrouAutomaticamente() {
        var turma = new Turma("Turma - 01", new Sala("1.10", 10));
        var dataHoje = LocalDate.now();
        turma.agendarPeriodo(dataHoje, dataHoje);

        Assertions.assertEquals(StatusTurma.ENCERRADO, turma.getStatus());


    }

    @Test
    public void deveMostrarQueTurmaEncerrouManualmente() {
        var turma = new Turma("Turma - 01", new Sala("1.10", 10));
        turma.agendarPeriodo(LocalDate.now(), null);

        turma.terminar();

        Assertions.assertEquals(StatusTurma.ENCERRADO, turma.getStatus());
    }

    @Test
    public void deveMostrarQueTurmaEstaEmAndamento() {
        var turma = new Turma("Turma - 01", new Sala("1.10", 10));
        turma.agendarPeriodo(LocalDate.now(), LocalDate.now().plusDays(1));

        Assertions.assertEquals(StatusTurma.EM_ANDAMENTO, turma.getStatus());

    }

    @Test
    public void deveMostrarQueTurmaCancelou() {
        var turma = new Turma("Turma - 01", new Sala("1.10", 10));
        turma.agendarPeriodo(LocalDate.now().plusDays(2), null);

        turma.terminar();

        Assertions.assertEquals(StatusTurma.CANCELADO, turma.getStatus());
    }

    @Test
    public void deveMostrarQueTurmaInterrompeu() {
        var turma = new Turma("Turma - 01", new Sala("1.10", 10));
        turma.agendarPeriodo(LocalDate.now(), LocalDate.now().plusDays(4));

        turma.terminar();

        Assertions.assertEquals(StatusTurma.INTERROMPIDA, turma.getStatus());
    }
}
