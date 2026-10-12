package br.org.bandasantabarbara.application.dtos.membros.responsaveis;

import br.org.bandasantabarbara.application.dtos.membros.MembroResponse;

import java.util.List;

public record MembroResponsaveisResponse(
        MembroResponse membro,
        List<ResponsavelResponse> responsaveis
) {
}
