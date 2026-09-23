package br.org.bandasantabarbara.application.dtos.convite;

import br.org.bandasantabarbara.application.dtos.membros.MembroResponse;
import br.org.bandasantabarbara.application.dtos.membros.MembroResumidoResponse;
import br.org.bandasantabarbara.model.ConviteStatus;

import java.time.Instant;
import java.util.UUID;

public record ConsultarConviteResponse(
        UUID idConvite,
        String URL,
        Instant criadoEm,
        Instant expiraEm,
        ConviteStatus status,
        MembroResumidoResponse membro
) {
}
