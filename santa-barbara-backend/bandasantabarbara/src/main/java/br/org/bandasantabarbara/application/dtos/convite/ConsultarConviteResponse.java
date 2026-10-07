package br.org.bandasantabarbara.application.dtos.convite;

import br.org.bandasantabarbara.application.dtos.membros.MembroResumidoResponse;
import br.org.bandasantabarbara.model.EnumConviteStatus;

import java.time.Instant;
import java.util.UUID;

public record ConsultarConviteResponse(
        UUID idConvite,
        String conviteURL,
        Instant criadoEm,
        Instant expiraEm,
        EnumConviteStatus status,
        MembroResumidoResponse membro
) {
}
