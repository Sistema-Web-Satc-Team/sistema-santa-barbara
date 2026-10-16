package br.org.bandasantabarbara.application.usecase.musica;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.musicas.MusicaResponse;
import br.org.bandasantabarbara.application.mapper.MusicaMapper;
import br.org.bandasantabarbara.application.mapper.PageMapper;
import br.org.bandasantabarbara.repositories.MusicaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ConsultarMusicaUseCase {

    private final MusicaRepository musicaRepository;
    private final MusicaMapper musicaMapper;
    private final PageMapper pageMapper;

    public ConsultarMusicaUseCase(MusicaRepository musicaRepository, MusicaMapper musicaMapper, PageMapper pageMapper) {
        this.musicaRepository = musicaRepository;
        this.musicaMapper = musicaMapper;
        this.pageMapper = pageMapper;
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
}
