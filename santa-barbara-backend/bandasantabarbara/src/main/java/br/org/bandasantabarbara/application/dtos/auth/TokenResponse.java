package br.org.bandasantabarbara.application.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados do token de autenticação.")
public record TokenResponse(
        @Schema(description = "Token JWT emitido.", format = "jwt")
        String token,
        @Schema(description = "Esquema de autenticação.", example = "Bearer")
        String tipo,
        @Schema(description = "Tempo de validade do token em segundos.", example = "3600")
        long expiraEmEmSegundos
) {
    public static TokenResponse bearer(String token, long expiraEmEmSegundos) {
        return new TokenResponse(token, "Bearer", expiraEmEmSegundos);
    }
}