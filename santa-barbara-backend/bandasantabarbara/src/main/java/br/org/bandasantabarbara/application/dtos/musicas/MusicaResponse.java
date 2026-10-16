package br.org.bandasantabarbara.application.dtos.musicas;

import br.org.bandasantabarbara.model.Genero;

import java.util.Set;
import java.util.UUID;

public record MusicaResponse(
        UUID id,
        String titulo,
        String compositor,
        Set<String> generos
) {
}
