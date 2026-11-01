package br.org.bandasantabarbara.application.services.musica;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;

import br.org.bandasantabarbara.application.dtos.partituras.PartituraRequest;
import br.org.bandasantabarbara.application.dtos.partituras.PartituraResponse;
import br.org.bandasantabarbara.application.mapper.PartituraMapper;
import br.org.bandasantabarbara.application.services.storage.FileService;
import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.musica.Instrumento;
import br.org.bandasantabarbara.model.musica.Partitura;
import br.org.bandasantabarbara.model.storage.StoredFile;
import br.org.bandasantabarbara.repositories.musica.InstrumentoRepository;
import br.org.bandasantabarbara.repositories.musica.MusicaRepository;
import br.org.bandasantabarbara.repositories.musica.PartituraRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PartituraService {
    private final MusicaRepository musicaRepository;
    private final PartituraRepository partituraRepository;
    private final FileService fileService;
    private final PartituraMapper partituraMapper;
    private final InstrumentoRepository instrumentoRepository;


    public PartituraService(
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
    public PartituraResponse adicionar(
        UUID musicaId, 
        MultipartFile file, 
        PartituraRequest request
    ) {

        if (file == null || file.isEmpty()) 
            throw new BadRequestException("Adicione o arquivo da partitura.");
        

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

        StoredFile arquivo = this.fileService.upload(file);
        partitura.setStoredFile(arquivo);

        this.partituraRepository.save(partitura);

        return this.partituraMapper.toResponse(partitura, this.getPartituraURL(arquivo));
    }

     @Transactional
    public PartituraResponse atualizar(UUID id, PartituraRequest request) {
        Partitura partitura = this.partituraRepository.findByIdWithFile(id)
                .orElseThrow(() -> new NotFoundException("Partitura não encontrada."));

        if (request.titulo() != null) {
            partitura.setTitulo(request.titulo());
        }

        if (request.arranjador() != null) {
            partitura.setArranjador(request.arranjador());
        }

        if (request.instrumentos() != null) {
            List<Instrumento> instrumentos = this.instrumentoRepository.findAllByNomeIn(request.instrumentos());

            if (instrumentos.size() < request.instrumentos().size()) {
                throw new BadRequestException("Alguns dos instrumentos informados não existem.");
            }

            partitura.setInstrumentos(instrumentos);
        }

        this.partituraRepository.save(partitura);

        String partituraUrl = partitura.getFile() != null
                ? getPartituraURL(partitura.getFile())
                : null;

        return this.partituraMapper.toResponse(partitura, partituraUrl);
    }

    @Transactional(readOnly = true)
    public List<PartituraResponse> listar(UUID musicaId) {
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

    @Transactional
    public void remover(UUID id) {
        Partitura partitura = this.partituraRepository.findByIdWithFile(id)
                .orElseThrow(() -> new NotFoundException("Partitura não encontrada."));

        this.partituraRepository.delete(partitura);

        if (partitura.getFile() != null) {
            this.fileService.delete(partitura.getFile());
        }
    }

    @Transactional(readOnly = true)
    public InputStream abrirPartitura(UUID fileId) {
        var partitura = this.partituraRepository.findByFileIdWithFile(fileId).orElseThrow(() -> new NotFoundException("Arquivo da partitura não encontrado."));
        return this.fileService.open(partitura.getFile());
    }

    private String getPartituraURL(StoredFile arquivo) {
        return this.fileService.urlForByResource("/partituras", arquivo);
    }
}
