package br.org.bandasantabarbara.application.usecase.musica;

import br.org.bandasantabarbara.application.dtos.musicas.MusicaRequest;
import br.org.bandasantabarbara.application.dtos.musicas.MusicaResponse;
import br.org.bandasantabarbara.application.mapper.MusicaMapper;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.Genero;
import br.org.bandasantabarbara.model.Musica;
import br.org.bandasantabarbara.repositories.GeneroRepository;
import br.org.bandasantabarbara.repositories.MusicaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class AtualizarMusicaUseCase {

    private final MusicaRepository musicaRepository;
    private final MusicaMapper musicaMapper;
    private final GeneroRepository generoRepository;

    public AtualizarMusicaUseCase(MusicaRepository musicaRepository, MusicaMapper musicaMapper, GeneroRepository generoRepository) {
        this.musicaRepository = musicaRepository;
        this.musicaMapper = musicaMapper;
        this.generoRepository = generoRepository;
    }

    @Transactional
    public MusicaResponse executar(UUID id, MusicaRequest request) {
        Musica musica = this.musicaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Música não encontrada"));

        this.musicaMapper.toUpdate(musica, request);
        if (request.generos() != null) {
            var slugs = request.generos();
            var generosExistentes = generoRepository.findAllBySlugIn(
                    new ArrayList<>(slugs)
            );

            var slugsExistentes = generosExistentes.stream()
                    .map(Genero::getSlug)
                    .collect(Collectors.toSet());

            var novosGeneros = slugs.stream()
                    .filter(slug -> !slugsExistentes.contains(slug))
                    .map(Genero::new)
                    .toList();

            var generosSalvos = generoRepository.saveAll(novosGeneros);
            var todosGeneros = new HashSet<>(generosExistentes);
            todosGeneros.addAll(generosSalvos);

            musica.setGeneros(todosGeneros);
        }

        return this.musicaMapper.toResponse(musica);
    }
}