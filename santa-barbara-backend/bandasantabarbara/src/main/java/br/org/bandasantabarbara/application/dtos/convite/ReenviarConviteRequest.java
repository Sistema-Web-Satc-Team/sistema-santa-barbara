package br.org.bandasantabarbara.application.dtos.convite;

import java.util.UUID;

import br.org.bandasantabarbara.application.services.membro.convite.EnumTipoCanal;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para reenviar um convite existente.")
public record ReenviarConviteRequest(
        @Schema(
                description = "Identificador do convite.", 
                format = "uuid", 
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        UUID idConvite,

        @Schema(
                description = "Canal pelo qual o convite será reenviado.", 
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        EnumTipoCanal canal
){ }
