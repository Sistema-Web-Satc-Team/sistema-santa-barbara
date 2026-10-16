package br.org.bandasantabarbara.application.usecase.musica;

import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.Musica;
import br.org.bandasantabarbara.repositories.MusicaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RemoverMusicaUseCase {

    private final MusicaRepository musicaRepository;

    public RemoverMusicaUseCase(MusicaRepository musicaRepository) {
        this.musicaRepository = musicaRepository;
    }

    @Transactional
    public void executar(UUID id) {
        Musica musica = this.musicaRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Música não encontrada"));

        this.musicaRepository.delete(musica);
    }
}