package br.org.bandasantabarbara.application.dtos.turmas;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import br.org.bandasantabarbara.model.turma.Turma.EnumTipoTurno;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;


public record AtualizarTurmaRequestDTO (

    @Schema(
        description = "Identificador único ou código da sala física.", 
        example = "1.10"
    )
    String codigoSala,

    @Schema(
        description = "Quantidade máxima de alunos permitida na turma.", 
        example = "35"
    )
    Integer numeroVagas,

    @FutureOrPresent(message = "A data de inicio precisa ser a partir da data de hoje.")
    @Schema(
        description = "Data de início das aulas (pode ser hoje ou data futura).", 
        example = "2026-11-01"
    )
    LocalDate dataInicio,

    @FutureOrPresent(message = "A data de inicio precisa ser a partir da data de hoje.")
    @Schema(
        description = "Data de encerramento das aulas.", 
        example = "2026-12-15"
    )
    LocalDate dataFim,
    
    @Schema(
        description = "Nome descritivo da turma.", 
        example = "Turma de Java Avançado - Noturno"
    )
    String nome,

    @Valid
    @Schema(
        description = "Turno da turma."
    )
    @JsonDeserialize(using = DayOfWeekDeserializer.class)
    EnumTipoTurno turno,

    @Valid
    @Schema(
        description = "Lista com os dias e horários em que as aulas acontecerão."
    )
    @JsonDeserialize(using = DayOfWeekDeserializer.class)
    List<HorarioRequestDTO> horarios
) {}