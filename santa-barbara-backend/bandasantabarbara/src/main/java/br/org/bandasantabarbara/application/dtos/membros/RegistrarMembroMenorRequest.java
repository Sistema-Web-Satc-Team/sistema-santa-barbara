package br.org.bandasantabarbara.application.dtos.membros;

import java.util.UUID;

import br.org.bandasantabarbara.model.membro.MembroResponsavel;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para cadastrar um membro menor de idade e associar seu responsável.")
public record RegistrarMembroMenorRequest(
        @Schema(description = "Dados cadastrais do menor.", requiredMode = Schema.RequiredMode.REQUIRED)
        MembroRequest menor,
        @Schema(description = "Identificador do membro responsável.", format = "uuid", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID idResponsavel,
        @Schema(description = "Grau de relação entre o menor e o responsável.", requiredMode = Schema.RequiredMode.REQUIRED)
        MembroResponsavel.TipoRelacaoResponsavel grauRelacao
) { }
