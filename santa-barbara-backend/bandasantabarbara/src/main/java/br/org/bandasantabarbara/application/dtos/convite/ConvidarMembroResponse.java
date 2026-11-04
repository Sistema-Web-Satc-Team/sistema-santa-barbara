package br.org.bandasantabarbara.application.dtos.convite;

import java.time.Instant;

import br.org.bandasantabarbara.model.membro.EnumConviteStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resultado da criação ou do reenvio de um convite.")
public record ConvidarMembroResponse(
        @Schema(description = "Identificador do convite.")
        String idConvite,
        @Schema(description = "URL para aceitar o convite.", format = "uri")
        String URL,
        @Schema(description = "Data e hora de criação.", format = "date-time")
        Instant criadoEm,
        @Schema(description = "Data e hora de expiração.", format = "date-time")
        Instant expiraEm,
        @Schema(description = "Status atual do convite.")
        EnumConviteStatus status
) {
}
