package br.org.bandasantabarbara.application.dtos.membros.responsaveis;

import br.org.bandasantabarbara.application.dtos.membros.MembroResponse;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Membro dependente e sua lista de responsáveis.")
public record MembroResponsaveisResponse(
        @Schema(description = "Dados do membro dependente.")
        MembroResponse membro,
        @Schema(description = "Responsáveis associados.")
        List<ResponsavelResponse> responsaveis
) {
}
