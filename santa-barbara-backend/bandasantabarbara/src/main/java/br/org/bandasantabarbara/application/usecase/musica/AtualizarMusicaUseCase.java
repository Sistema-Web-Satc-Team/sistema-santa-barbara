package br.org.bandasantabarbara.application.usecase.musica;

import br.org.bandasantabarbara.application.dtos.musicas.MusicaRequest;
import br.org.bandasantabarbara.application.dtos.musicas.MusicaResponse;
import br.org.bandasantabarbara.application.mapper.MusicaMapper;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.Musica;
import br.org.bandasantabarbara.repositories.MusicaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
public class AtualizarMusicaUseCase {

    private final MusicaRepository musicaRepository;
    private final MusicaMapper musicaMapper;

    public AtualizarMusicaUseCase(MusicaRepository musicaRepository, MusicaMapper musicaMapper) {
        this.musicaRepository = musicaRepository;
        this.musicaMapper = musicaMapper;
    }

    @Transactional
    public MusicaResponse executar(UUID id, MusicaRequest request) {
        Musica musica = this.musicaRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Música não encontrada"));

        this.musicaMapper.toUpdate(musica, request);

        return this.musicaMapper.toResponse(musica);
    }
}