package br.org.bandasantabarbara.application.dtos.convite;

import java.util.UUID;

import br.org.bandasantabarbara.application.services.membro.convite.EnumTipoCanal;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para convidar um membro por um canal de envio.")
public record ConvidarMembroRequest(
        @Schema(description = "Identificador do membro destinatário.", format = "uuid", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID idMembro,
        @Schema(description = "Canal pelo qual o convite será enviado.", requiredMode = Schema.RequiredMode.REQUIRED)
        EnumTipoCanal canal
) {
}
