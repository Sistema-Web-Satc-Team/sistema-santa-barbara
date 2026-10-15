package br.org.bandasantabarbara.application.usecase.auth;

import br.org.bandasantabarbara.application.services.storage.FileService;
import br.org.bandasantabarbara.model.StoredFile;
import br.org.bandasantabarbara.repositories.MembroRepository;
import br.org.bandasantabarbara.repositories.StoredFileRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AtribuirFotoPerfilUseCase {

    private final FileService fileService;
    private final MembroRepository membroRepository;


    public AtribuirFotoPerfilUseCase(FileService fileService, MembroRepository membroRepository) {
        this.fileService = fileService;
        this.membroRepository = membroRepository;
    }


    @Transactional
    public void execute(UUID membroId, MultipartFile file) {
        var membro = membroRepository.findById(membroId)
                .orElseThrow(() -> new RuntimeException("Ocorreu um erro interno."));

        StoredFile antigo = membro.getAvatarFile();
        StoredFile novo = fileService.upload(file);

        membro.setAvatarFile(novo);
        membroRepository.flush();

        if (antigo != null) {
            fileService.delete(antigo.getId());
        }
    }
}
