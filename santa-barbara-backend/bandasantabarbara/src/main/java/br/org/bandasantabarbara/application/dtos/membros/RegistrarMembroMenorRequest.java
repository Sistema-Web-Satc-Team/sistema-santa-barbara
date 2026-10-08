package br.org.bandasantabarbara.application.dtos.membros;

import java.util.UUID;

public record RegistrarMembroMenorRequest(
        MembroRequest menor,
        UUID idResponsavel,
        String grauRelacao
) { }
