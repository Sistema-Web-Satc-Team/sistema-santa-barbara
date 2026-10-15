package br.org.bandasantabarbara.application.dtos.membros.responsaveis;

import br.org.bandasantabarbara.model.MembroResponsavel;

public record MembroRelacaoRequest(
        MembroResponsavel.TipoRelacaoResponsavel grauRelacao
) {
}
