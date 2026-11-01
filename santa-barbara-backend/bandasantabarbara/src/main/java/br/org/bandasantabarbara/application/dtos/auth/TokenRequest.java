package br.org.bandasantabarbara.application.dtos.auth;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados usados internamente para gerar um token de acesso.")
public record TokenRequest(
        @Schema(description = "Identificador do membro.", format = "uuid")
        String id,
        @Schema(description = "E-mail do membro.", example = "usuario@santabarbara.org")
        String email,
        @Schema(description = "Nome de usuário.", example = "usuario")
        String nomeUsuario,
        @Schema(description = "Papéis ativos do membro.")
        List<String> papeis
)
{ }
