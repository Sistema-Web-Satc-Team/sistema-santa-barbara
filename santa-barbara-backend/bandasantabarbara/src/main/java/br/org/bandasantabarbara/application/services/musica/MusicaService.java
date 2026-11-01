package br.org.bandasantabarbara.application.services.musica;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.musicas.MusicaRequest;
import br.org.bandasantabarbara.application.dtos.musicas.MusicaResponse;
import br.org.bandasantabarbara.application.mapper.MusicaMapper;
import br.org.bandasantabarbara.application.mapper.PageMapper;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.musica.Genero;
import br.org.bandasantabarbara.model.musica.Musica;
import br.org.bandasantabarbara.repositories.musica.GeneroRepository;
import br.org.bandasantabarbara.repositories.musica.MusicaRepository;

@Service
public class MusicaService {
    
    private final MusicaRepository musicaRepository;
    private final GeneroRepository generoRepository;

    private final MusicaMapper musicaMapper;
    private final PageMapper pageMapper;

    public MusicaService(
        MusicaRepository musicaRepository,
        GeneroRepository generoRepository,
        MusicaMapper musicaMapper,
        PageMapper pageMapper
    ) {
        this.musicaRepository = musicaRepository;
        this.generoRepository = generoRepository;
        this.musicaMapper = musicaMapper;
        this.pageMapper = pageMapper;
    }

    @Transactional
    public MusicaResponse cadastrar(MusicaRequest request) {
        var musica = this.musicaMapper.toEntity(request);

        List<String> slugs = musica.getGeneros().stream().map(Genero::getSlug).toList();

        List<Genero> generosExistentes = this.generoRepository.findAllBySlugIn(slugs);

        Set<String> slugsExistentes = generosExistentes.stream()
                .map(Genero::getSlug)
                .collect(Collectors.toSet());

        List<Genero> novosGeneros = musica.getGeneros().stream()
                .filter(g -> !slugsExistentes.contains(g.getSlug()))
                .toList();

        if (!novosGeneros.isEmpty()) {
            List<Genero> generosSalvos = this.generoRepository.saveAll(novosGeneros);
            generosExistentes.addAll(generosSalvos);
        }

        musica.setGeneros(generosExistentes);

        this.musicaRepository.save(musica);
        return this.musicaMapper.toResponse(musica);
    }

    @Transactional
    public MusicaResponse atualizar(UUID id, MusicaRequest request) {
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

    public PageResponse<MusicaResponse> listar(String search, Pageable pageable) {
        String searchTratado = null;
        if (search != null && !search.isBlank()) {
            searchTratado = search
                    .replace("\\", "\\\\")
                    .replace("%", "\\%")
                    .replace("_", "\\_");
        }

        return this.pageMapper.toResponse(this.musicaRepository.findAll(searchTratado, pageable).map(musicaMapper::toResponse));

    }


    @Transactional
    public void executar(UUID id) {
        Musica musica = this.musicaRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Música não encontrada"));

        this.musicaRepository.delete(musica);
    }
}
