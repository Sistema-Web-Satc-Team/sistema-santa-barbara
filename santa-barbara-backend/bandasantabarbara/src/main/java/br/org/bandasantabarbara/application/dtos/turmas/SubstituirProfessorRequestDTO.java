package br.org.bandasantabarbara.application.dtos.turmas;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Schema(description = "Identificador do professor que substituirá o vínculo ativo.")
public record SubstituirProfessorRequestDTO(
    @NotNull
    @Schema(description = "ID do novo professor.")
    UUID idNovoProfessor,

    @NotNull 
    @Schema(description = "Justificativa da substituição do professor.")
    String justificativa
) {}
