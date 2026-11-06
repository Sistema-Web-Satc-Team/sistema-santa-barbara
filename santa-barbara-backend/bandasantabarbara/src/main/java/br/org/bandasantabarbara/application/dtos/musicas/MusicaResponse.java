package br.org.bandasantabarbara.application.dtos.musicas;

import java.util.Set;
import java.util.UUID;

import br.org.bandasantabarbara.model.musica.Genero;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de uma música cadastrada.")
public record MusicaResponse(
        @Schema(description = "Identificador da música.", format = "uuid")
        UUID id,
        @Schema(description = "Título da música.", example = "Canção de exemplo")
        String titulo,
        @Schema(description = "Compositor da música.", example = "Compositor")
        String compositor,
        @Schema(description = "Gêneros associados à música.")
        Set<String> generos
) {
}
