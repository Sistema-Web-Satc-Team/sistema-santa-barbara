package br.org.bandasantabarbara.application.dtos.membros;

import br.org.bandasantabarbara.model.MembroResponsavel;

import java.util.UUID;

public record RegistrarMembroMenorRequest(
        MembroRequest menor,
        UUID idResponsavel,
        MembroResponsavel.TipoRelacaoResponsavel grauRelacao
) { }
