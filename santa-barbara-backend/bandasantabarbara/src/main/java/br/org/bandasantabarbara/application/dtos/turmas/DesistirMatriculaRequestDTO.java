package br.org.bandasantabarbara.application.dtos.turmas;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Justificativa opcional para desistir de uma matrícula.")
public record DesistirMatriculaRequestDTO(
    @Schema(description = "Motivo da desistência.")
    String justificativa
) {
}
