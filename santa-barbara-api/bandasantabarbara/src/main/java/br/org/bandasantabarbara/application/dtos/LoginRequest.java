package br.org.bandasantabarbara.application.dtos;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "O login ou e-mail é obrigatório.")
        String login,

        @NotBlank(message = "A senha é obrigatória.")
        String senha
) {}