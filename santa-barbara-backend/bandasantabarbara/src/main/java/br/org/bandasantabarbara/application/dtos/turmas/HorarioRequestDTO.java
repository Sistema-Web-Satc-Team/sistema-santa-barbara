package br.org.bandasantabarbara.application.dtos.turmas;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Representa um intervalo de aula em determinado dia da semana.")
public record HorarioRequestDTO(

    @NotNull(message = "O dia da semana é obrigatório.")
    @JsonDeserialize(using = DayOfWeekDeserializer.class)
    @Schema(
        description = "Dia da semana em que ocorrerá a aula.",
        example = "SEGUNDA",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    DayOfWeek diaDaSemana,

    @NotNull(message = "O horário de início é obrigatório.")
    @Schema(
        description = "Horário de início da aula.",
        type = "string",
        format = "time",
        example = "19:00",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    LocalTime inicio,

    @NotNull(message = "O horário de término é obrigatório.")
    @Schema(
        description = "Horário de término da aula.",
        type = "string",
        format = "time",
        example = "22:00",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    LocalTime fim

) {}