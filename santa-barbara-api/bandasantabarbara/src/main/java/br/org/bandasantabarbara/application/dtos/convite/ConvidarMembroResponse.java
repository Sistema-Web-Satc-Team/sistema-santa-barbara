package br.org.bandasantabarbara.application.dtos.convite;

import java.time.Instant;

public record ConvidarMembroResponse(
        String idConvite,
        String URL,
        Instant criadoEm,
        Instant expiraEm
) {
}
