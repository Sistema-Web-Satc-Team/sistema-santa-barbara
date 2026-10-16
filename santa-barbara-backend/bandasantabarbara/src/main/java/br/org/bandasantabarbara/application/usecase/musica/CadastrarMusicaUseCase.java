package br.org.bandasantabarbara.application.usecase.musica;

import br.org.bandasantabarbara.application.dtos.musicas.MusicaRequest;
import br.org.bandasantabarbara.application.dtos.musicas.MusicaResponse;
import br.org.bandasantabarbara.application.mapper.MusicaMapper;
import br.org.bandasantabarbara.model.Genero;
import br.org.bandasantabarbara.repositories.GeneroRepository;
import br.org.bandasantabarbara.repositories.MusicaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CadastrarMusicaUseCase {

    private final MusicaRepository musicaRepository;
    private final GeneroRepository generoRepository;
    private final MusicaMapper musicaMapper;

    public CadastrarMusicaUseCase(
        MusicaRepository musicaRepository,
        GeneroRepository generoRepository,
        MusicaMapper musicaMapper
    ) {
        this.musicaRepository = musicaRepository;
        this.generoRepository = generoRepository;
        this.musicaMapper = musicaMapper;
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

}
