package br.org.bandasantabarbara.application.usecase.partitura;

import br.org.bandasantabarbara.application.dtos.partituras.PartituraRequest;
import br.org.bandasantabarbara.application.dtos.partituras.PartituraResponse;
import br.org.bandasantabarbara.application.mapper.PartituraMapper;
import br.org.bandasantabarbara.application.services.storage.FileService;
import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.Instrumento;
import br.org.bandasantabarbara.repositories.InstrumentoRepository;
import br.org.bandasantabarbara.repositories.MusicaRepository;
import br.org.bandasantabarbara.repositories.PartituraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class AdicionarPartituraMusicaUseCase {

    private final MusicaRepository musicaRepository;
    private final PartituraRepository partituraRepository;
    private final FileService fileService;
    private final PartituraMapper partituraMapper;
    private final InstrumentoRepository instrumentoRepository;


    public AdicionarPartituraMusicaUseCase(
            MusicaRepository musicaRepository,
            PartituraRepository partituraRepository,
            PartituraMapper partituraMapper,
            FileService fileService,
            InstrumentoRepository instrumentoRepository
    ) {
        this.musicaRepository = musicaRepository;
        this.partituraRepository = partituraRepository;
        this.partituraMapper = partituraMapper;
        this.fileService = fileService;
        this.instrumentoRepository = instrumentoRepository;
    }

    @Transactional
    public PartituraResponse adicionar(UUID musicaId, MultipartFile file, PartituraRequest request) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Adicione o arquivo da partitura.");
        }

        var musica = this.musicaRepository.findById(musicaId).orElseThrow(
                () -> new NotFoundException("Música informada não existe.")
        );

        var partitura = this.partituraMapper.toEntity(request, musica);

        if (request.instrumentos() != null) {
            List<Instrumento> instrumentos = this.instrumentoRepository.findAllByNomeIn(request.instrumentos());

            if (instrumentos.size() < request.instrumentos().size()) {
                throw  new BadRequestException("Alguns dos instrumentos informados não existem.");
            }

            partitura.setInstrumentos(instrumentos);
        }

        var arquivo = this.fileService.upload(file);

        String partituraUrl = this.fileService.urlForByResource("/partituras",arquivo);

        partitura.setStoredFile(arquivo);

        this.partituraRepository.save(partitura);

        return this.partituraMapper.toResponse(partitura, partituraUrl);
    }

}
