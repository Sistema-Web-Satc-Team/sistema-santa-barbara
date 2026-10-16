package br.org.bandasantabarbara.application.usecase.partitura;

import br.org.bandasantabarbara.application.services.storage.FileService;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.StoredFile;
import br.org.bandasantabarbara.repositories.PartituraRepository;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class PegarArquivoPartituraUseCase {

    private final FileService fileService;
    private final PartituraRepository partituraRepository;

    public PegarArquivoPartituraUseCase(
            FileService fileService,
            PartituraRepository partituraRepository
    ) {
        this.fileService = fileService;
        this.partituraRepository = partituraRepository;
    }

    @Transactional(readOnly = true)
    public InputStream open(UUID fileId) {
        var partitura = this.partituraRepository.findByFileIdWithFile(fileId).orElseThrow(() -> new NotFoundException("Arquivo da partitura não encontrado."));
        return this.fileService.open(partitura.getFile());
    }
}
