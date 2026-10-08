package br.org.bandasantabarbara.application.dtos.membros;

import java.util.List;

public record MembroVinculosResponse(
        MembroResponse membro,
        List<VinculoResponse> vinculos
) {}