package br.org.bandasantabarbara.unit;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.model.aula.Aula;
import br.org.bandasantabarbara.model.ensino.Professor;
import br.org.bandasantabarbara.model.turma.Sala;
import br.org.bandasantabarbara.model.turma.Turma;

import org.assertj.core.api.Assertions;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Disabled;

@Disabled("Módulo de aulas temporariamente fora do escopo da primeira entrega")
@ActiveProfiles("test")
class AulaTest {

    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 6, 15, 14, 40);
    private static final LocalDateTime FIM_PREVISTO = LocalDateTime.of(2026, 6, 15, 15, 0);

    @Test
    void deveIniciarAulaNormalmente() {
        Turma turma = new Turma("Testando", new Sala("1.10", 10));

        Aula aula = Aula.iniciar(turma, new Professor(), INICIO, FIM_PREVISTO);

        Assertions.assertThat(aula.getDataHoraInicio()).isEqualTo(INICIO);
        Assertions.assertThat(aula.getDataHoraFimPrevista()).isEqualTo(FIM_PREVISTO);
        Assertions.assertThat(aula.getDataHoraFim()).isNull();
        Assertions.assertThat(aula.getStatusAula())
            .isEqualTo(Aula.EnumStatusAula.EM_ANDAMENTO);
    }

    @Test
    void deveFinalizarAulaNormalmente() {
        Turma turma = new Turma("Testando", new Sala("1.10", 10));
        Aula aula = Aula.iniciar(turma, new Professor(), INICIO, FIM_PREVISTO);

        aula.finalizarManualmente(INICIO.plusMinutes(5));

        Assertions.assertThat(aula.getDataHoraFim()).isEqualTo(INICIO.plusMinutes(5));
        Assertions.assertThat(aula.getTipoEncerramento()).isEqualTo(Aula.EnumTipoEncerramento.MANUAL);
        Assertions.assertThat(aula.getStatusAula())
            .isEqualTo(Aula.EnumStatusAula.FINALIZADA);
    }

    @Test
    void naoDeveFinalizarAulaDuasVezes() {
        Aula aula = Aula.iniciar(
            new Turma("Testando", new Sala("1.10", 10)),
            new Professor(),
            INICIO,
            FIM_PREVISTO
        );
        aula.finalizarManualmente(INICIO.plusMinutes(5));

        Assertions.assertThatThrownBy(() -> aula.finalizarManualmente(INICIO.plusMinutes(6)))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("A aula já foi finalizada.");
    }

    @Test
    void podeReabrirAulaFinalizadaManualmenteAntesDoFimPrevisto() {
        Aula aula = Aula.iniciar(
            new Turma("Testando", new Sala("1.10", 10)),
            new Professor(),
            INICIO,
            FIM_PREVISTO
        );
        aula.finalizarManualmente(INICIO.plusMinutes(5));

        aula.reabrir(INICIO.plusMinutes(10));

        Assertions.assertThat(aula.getStatusAula()).isEqualTo(Aula.EnumStatusAula.EM_ANDAMENTO);
        Assertions.assertThat(aula.getDataHoraFim()).isNull();
        Assertions.assertThat(aula.getTipoEncerramento()).isNull();
    }

    @Test
    void naoPodeReabrirAulaEncerradaPorTempoEsgotado() {
        Aula aula = Aula.iniciar(
            new Turma("Testando", new Sala("1.10", 10)),
            new Professor(),
            INICIO,
            FIM_PREVISTO
        );
        aula.finalizarPorTempoEsgotado();

        Assertions.assertThat(aula.getDataHoraFim()).isEqualTo(FIM_PREVISTO);
        Assertions.assertThat(aula.getTipoEncerramento()).isEqualTo(Aula.EnumTipoEncerramento.TEMPO_ESGOTADO);
        Assertions.assertThatThrownBy(() -> aula.reabrir(FIM_PREVISTO))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Apenas aulas finalizadas manualmente podem ser reabertas.");
    }

    @Test
    void naoPodeReabrirAulaDepoisDoFimPrevisto() {
        Aula aula = Aula.iniciar(
            new Turma("Testando", new Sala("1.10", 10)),
            new Professor(),
            INICIO,
            FIM_PREVISTO
        );
        aula.finalizarManualmente(INICIO.plusMinutes(5));

        Assertions.assertThatThrownBy(() -> aula.reabrir(FIM_PREVISTO.plusSeconds(1)))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("O horário previsto para a aula já foi ultrapassado.");
    }
}