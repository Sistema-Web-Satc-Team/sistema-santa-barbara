package br.org.bandasantabarbara.application.dtos.papel;

import jakarta.validation.constraints.NotNull;

public record ExcluirPapelRequest(@NotNull String papel) {
}
