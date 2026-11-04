package br.org.bandasantabarbara.application.dtos.convite;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados necessários para aceitar um convite e criar as credenciais de acesso.")
public record AceitarConviteRequest(
        @NotNull
        @Schema(description = "Identificador do convite.", format = "uuid", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID idConvite,

        @NotBlank(message = "A senha é obrigatória")
        @Schema(description = "Senha que será usada para acessar a conta.", format = "password", requiredMode = Schema.RequiredMode.REQUIRED)
        String senha,

        @NotBlank(message = "O nome de usuário não pode estar em branco")
        @Size(min = 2, max = 20, message = "O usuário deve ter entre 2 e 20 caracteres")
        @Schema(description = "Nome de usuário desejado (de 2 a 20 caracteres).", example = "maria.silva", minLength = 2, maxLength = 20, requiredMode = Schema.RequiredMode.REQUIRED)
        String nomeDeUsuario
) {}
