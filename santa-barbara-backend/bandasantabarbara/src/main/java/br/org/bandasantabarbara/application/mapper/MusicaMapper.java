package br.org.bandasantabarbara.application.mapper;


import br.org.bandasantabarbara.application.dtos.musicas.MusicaRequest;
import br.org.bandasantabarbara.application.dtos.musicas.MusicaResponse;
import br.org.bandasantabarbara.model.Genero;
import br.org.bandasantabarbara.model.Musica;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MusicaMapper {

    public Musica toEntity(MusicaRequest request) {

        var musica = new Musica(request.titulo());

        if (request.compositor() != null) {
            musica.setCompositor(request.compositor());
        }

        if (!request.generos().isEmpty()) {
            var generos = request.generos().stream().map(Genero::new).collect(Collectors.toSet());
            musica.setGeneros(generos);
        }

        return musica;
    }

    public MusicaResponse toResponse(Musica musica) {
        return new MusicaResponse(
            musica.getId(),
            musica.getTitulo(),
            musica.getCompositor(),
            musica.getGeneros().stream().map(Genero::getNome).collect(Collectors.toSet())
        );
    }
}
