package br.org.bandasantabarbara.application.usecase.partitura;

import br.org.bandasantabarbara.application.dtos.partituras.PartituraRequest;
import br.org.bandasantabarbara.application.dtos.partituras.PartituraResponse;
import br.org.bandasantabarbara.application.mapper.PartituraMapper;
import br.org.bandasantabarbara.application.services.storage.FileService;
import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.Instrumento;
import br.org.bandasantabarbara.model.Partitura;
import br.org.bandasantabarbara.repositories.InstrumentoRepository;
import br.org.bandasantabarbara.repositories.PartituraRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
@Slf4j
@Service
public class AtualizarPartituraUseCase {

    private final PartituraRepository partituraRepository;
    private final InstrumentoRepository instrumentoRepository;
    private final PartituraMapper partituraMapper;
    private final FileService fileService;

    public AtualizarPartituraUseCase(
            PartituraRepository partituraRepository,
            InstrumentoRepository instrumentoRepository,
            PartituraMapper partituraMapper,
            FileService fileService
    ) {
        this.partituraRepository = partituraRepository;
        this.instrumentoRepository = instrumentoRepository;
        this.partituraMapper = partituraMapper;
        this.fileService = fileService;
    }

    @Transactional
    public PartituraResponse executar(UUID id, PartituraRequest request) {
        Partitura partitura = this.partituraRepository.findByIdWithFile(id)
                .orElseThrow(() -> new NotFoundException("Partitura não encontrada."));

        log.info("Título vindo do request para: {}", request.titulo());
        if (request.titulo() != null) {
            partitura.setTitulo(request.titulo());
            log.info("Título atualizado para: {}", partitura.getTitulo());
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
                ? this.fileService.urlForByResource("/partituras", partitura.getFile())
                : null;

        return this.partituraMapper.toResponse(partitura, partituraUrl);
    }
}