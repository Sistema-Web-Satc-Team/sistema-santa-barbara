package br.org.bandasantabarbara.application.dtos.convite;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AceitarConviteRequest(
        @NotNull
        UUID idConvite,

        @NotBlank(message = "A senha é obrigatória")
        String senha,

        @NotBlank(message = "O nome de usuário não pode estar em branco")
        @Size(min = 2, max = 20, message = "O usuário deve ter entre 2 e 20 caracteres")
        String nomeDeUsuario
) {}
