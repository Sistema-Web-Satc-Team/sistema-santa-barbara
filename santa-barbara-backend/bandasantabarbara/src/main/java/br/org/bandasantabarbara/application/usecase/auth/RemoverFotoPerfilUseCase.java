package br.org.bandasantabarbara.application.usecase.auth;

import br.org.bandasantabarbara.application.services.storage.FileService;
import br.org.bandasantabarbara.model.StoredFile;
import br.org.bandasantabarbara.repositories.MembroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
public class RemoverFotoPerfilUseCase {

    private final FileService fileService;
    private final MembroRepository membroRepository;


    public RemoverFotoPerfilUseCase(FileService fileService, MembroRepository membroRepository) {
        this.fileService = fileService;
        this.membroRepository = membroRepository;
    }



    @Transactional
    public void execute(UUID membroId) {
        var membro = membroRepository.findById(membroId)
                .orElseThrow(() -> new RuntimeException(
                        "Ocorreu um erro interno."));

        StoredFile avatar = membro.getAvatarFile();
        if (avatar == null) {
            return;
        }

        membro.setAvatarFile(null);
        membroRepository.flush();

        fileService.delete(avatar.getId());
    }
}
