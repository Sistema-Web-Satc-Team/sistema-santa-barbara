package br.org.bandasantabarbara.application.dtos.turmas;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Schema(description = "Dados para matricular um aluno em uma turma.")
public record MatriculaRequestDTO(
    @NotNull
    @Schema(description = "ID do aluno.", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID idAluno,

    @NotNull
    @Schema(description = "ID da turma.", example = "9402")
    Long idTurma
) {
}
