package br.org.bandasantabarbara.application.dtos.membros.responsaveis;

import br.org.bandasantabarbara.model.membro.MembroResponsavel;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados da relação entre um membro dependente e seu responsável.")
public record MembroRelacaoRequest(
        @Schema(description = "Grau de relação do responsável com o membro.", example = "MAE", requiredMode = Schema.RequiredMode.REQUIRED)
        MembroResponsavel.TipoRelacaoResponsavel grauRelacao
) {
}
