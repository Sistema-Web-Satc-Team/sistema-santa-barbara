package br.org.bandasantabarbara.application.usecase.partitura;

import br.org.bandasantabarbara.application.services.storage.FileService;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.Partitura;
import br.org.bandasantabarbara.repositories.PartituraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RemoverPartituraUseCase {

    private final PartituraRepository partituraRepository;
    private final FileService fileService;

    public RemoverPartituraUseCase(
            PartituraRepository partituraRepository,
            FileService fileService
    ) {
        this.partituraRepository = partituraRepository;
        this.fileService = fileService;
    }

    @Transactional
    public void executar(UUID id) {
        Partitura partitura = this.partituraRepository.findByIdWithFile(id)
                .orElseThrow(() -> new NotFoundException("Partitura não encontrada."));

        this.partituraRepository.delete(partitura);

        if (partitura.getFile() != null) {
            this.fileService.delete(partitura.getFile());
        }
    }
}
