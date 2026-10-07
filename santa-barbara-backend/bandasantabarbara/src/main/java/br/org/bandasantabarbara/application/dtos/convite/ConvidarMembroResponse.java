package br.org.bandasantabarbara.application.dtos.convite;

import br.org.bandasantabarbara.model.EnumConviteStatus;

import java.time.Instant;

public record ConvidarMembroResponse(
        String idConvite,
        String URL,
        Instant criadoEm,
        Instant expiraEm,
        EnumConviteStatus status
) {
}
