package br.org.bandasantabarbara.application.dtos.sala;

import jakarta.validation.constraints.NotNull;

public record SalaRequestDTO(
        @NotNull()
        String code,

        @NotNull()
        Integer capacidade
) {}
