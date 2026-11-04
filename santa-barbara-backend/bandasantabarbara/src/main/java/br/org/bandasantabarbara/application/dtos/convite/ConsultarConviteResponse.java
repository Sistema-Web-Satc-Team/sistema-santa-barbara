package br.org.bandasantabarbara.application.dtos.convite;

import br.org.bandasantabarbara.application.dtos.membros.MembroResumidoResponse;
import br.org.bandasantabarbara.model.membro.EnumConviteStatus;

import java.time.Instant;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detalhes de um convite enviado a um membro.")
public record ConsultarConviteResponse(
        @Schema(description = "Identificador do convite.", format = "uuid")
        UUID idConvite,
        @Schema(description = "URL para aceitar o convite.", format = "uri")
        String conviteURL,
        @Schema(description = "Data e hora de criação.", format = "date-time")
        Instant criadoEm,
        @Schema(description = "Data e hora de expiração.", format = "date-time")
        Instant expiraEm,
        @Schema(description = "Status atual do convite.")
        EnumConviteStatus status,
        @Schema(description = "Membro destinatário.")
        MembroResumidoResponse membro
) {
}
