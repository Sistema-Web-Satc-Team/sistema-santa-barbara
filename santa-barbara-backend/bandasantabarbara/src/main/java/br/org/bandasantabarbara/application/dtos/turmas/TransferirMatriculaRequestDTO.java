package br.org.bandasantabarbara.application.dtos.turmas;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para transferir uma matrícula ativa para outra turma.")
public record TransferirMatriculaRequestDTO(
    @NotNull
    @Schema(description = "ID da turma de destino.", example = "4")
    Long idNovaTurma,

    @Schema(description = "Justificativa para a transferência.")
    String justificativa
) {}
