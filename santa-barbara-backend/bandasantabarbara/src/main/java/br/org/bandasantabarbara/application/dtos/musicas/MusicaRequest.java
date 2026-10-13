package br.org.bandasantabarbara.application.dtos.musicas;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record MusicaRequest (
        @NotBlank
        @Schema(
                description = "Título da música",
                example = "Evidências",
                minLength = 2,
                maxLength = 154,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @Size(min = 2, message = "Título deve ter pelo menos {min} caracteres.")
        @Size(max = 154, message = "Número máximo de caracteres excedido.")
        String titulo,

        @Schema(
                description = "Nome do compositor da música",
                example = "José Augusto",
                minLength = 2,
                maxLength = 154,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Size(min = 2, message = "Compositor deve ter pelo menos {min} caracteres.")
        @Size(max = 154, message = "Número máximo de caracteres excedido.")
        String compositor,

        @Schema(
                description = "Lista de slugs dos gêneros que serão associados à música",
                example = "[\"sertanejo\", \"pop\"]",
                maxLength = 24,
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        @Size(max = 24, message = "O cadastro inicial de música permite no máximo {max} gêneros apenas.")
        Set<String> generos
) {
}
