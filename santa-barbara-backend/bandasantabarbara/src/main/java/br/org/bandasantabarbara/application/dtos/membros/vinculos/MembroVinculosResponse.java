package br.org.bandasantabarbara.application.dtos.membros.vinculos;

import br.org.bandasantabarbara.application.dtos.membros.MembroResponse;

public record MembroVinculosResponse(
        MembroResponse membro,
        VinculosResponse vinculos
) {}