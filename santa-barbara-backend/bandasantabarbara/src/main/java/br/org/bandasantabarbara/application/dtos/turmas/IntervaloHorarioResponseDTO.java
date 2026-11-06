package br.org.bandasantabarbara.application.dtos.turmas;

import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representa um intervalo de horário de aula.")
public record IntervaloHorarioResponseDTO(

    @Schema(
        description = "Horário de início da aula.",
        type = "string",
        format = "time",
        example = "19:00"
    )
    LocalTime horaInicio,

    @Schema(
        description = "Horário de término da aula.",
        type = "string",
        format = "time",
        example = "22:00"
    )
    LocalTime horaFim

) {}