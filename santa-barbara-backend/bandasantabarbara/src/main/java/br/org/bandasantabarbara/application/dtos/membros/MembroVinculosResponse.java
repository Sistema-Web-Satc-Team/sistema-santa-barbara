package br.org.bandasantabarbara.application.dtos.membros;

public record MembroVinculosResponse(
        MembroResponse membro,
        VinculosResponse vinculos
) {}