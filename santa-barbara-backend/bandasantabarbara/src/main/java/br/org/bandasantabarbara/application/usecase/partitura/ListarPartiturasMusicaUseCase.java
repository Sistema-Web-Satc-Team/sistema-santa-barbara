package br.org.bandasantabarbara.application.usecase.partitura;

import br.org.bandasantabarbara.application.dtos.partituras.PartituraResponse;
import br.org.bandasantabarbara.application.mapper.PartituraMapper;
import br.org.bandasantabarbara.application.services.storage.FileService;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.Partitura;
import br.org.bandasantabarbara.repositories.MusicaRepository;
import br.org.bandasantabarbara.repositories.PartituraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ListarPartiturasMusicaUseCase {

    private final MusicaRepository musicaRepository;
    private final PartituraRepository partituraRepository;
    private final PartituraMapper partituraMapper;
    private final FileService fileService;

    public ListarPartiturasMusicaUseCase(
            MusicaRepository musicaRepository,
            PartituraRepository partituraRepository,
            PartituraMapper partituraMapper,
            FileService fileService
    ) {
        this.musicaRepository = musicaRepository;
        this.partituraRepository = partituraRepository;
        this.partituraMapper = partituraMapper;
        this.fileService = fileService;
    }

    @Transactional(readOnly = true)
    public List<PartituraResponse> executar(UUID musicaId) {
        if (!this.musicaRepository.existsById(musicaId)) {
            throw new NotFoundException("Música informada não existe.");
        }

        List<Partitura> partituras = this.partituraRepository.findByMusicaId(musicaId);

        return partituras.stream().map(partitura -> {
            String url = partitura.getFile() != null
                    ? this.fileService.urlForByResource("/partituras", partitura.getFile())
                    : null;
            return this.partituraMapper.toResponse(partitura, url);
        }).toList();
    }
}