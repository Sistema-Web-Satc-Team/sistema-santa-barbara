package br.org.bandasantabarbara.application.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Credenciais usadas para autenticar um membro.")
public record LoginRequest(
        @NotBlank(message = "O login ou e-mail é obrigatório.")
        @Schema(description = "Nome de usuário ou e-mail cadastrado.", example = "usuario@santabarbara.org", requiredMode = Schema.RequiredMode.REQUIRED)
        String login,

        @NotBlank(message = "A senha é obrigatória.")
        @Schema(description = "Senha da conta.", format = "password", example = "SenhaSegura123", requiredMode = Schema.RequiredMode.REQUIRED)
        String senha
) {}