package br.org.bandasantabarbara.application.dtos.turmas;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Horários semanais de funcionamento de uma turma.")
public record HorarioResponseDTO(

    @Schema(description = "Horários de segunda-feira.")
    List<IntervaloHorarioResponseDTO> segunda,

    @Schema(description = "Horários de terça-feira.")
    List<IntervaloHorarioResponseDTO> terca,

    @Schema(description = "Horários de quarta-feira.")
    List<IntervaloHorarioResponseDTO> quarta,

    @Schema(description = "Horários de quinta-feira.")
    List<IntervaloHorarioResponseDTO> quinta,

    @Schema(description = "Horários de sexta-feira.")
    List<IntervaloHorarioResponseDTO> sexta,

    @Schema(description = "Horários de sábado.")
    List<IntervaloHorarioResponseDTO> sabado,

    @Schema(description = "Horários de domingo.")
    List<IntervaloHorarioResponseDTO> domingo

) {}