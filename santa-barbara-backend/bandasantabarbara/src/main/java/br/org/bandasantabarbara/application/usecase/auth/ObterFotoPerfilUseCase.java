package br.org.bandasantabarbara.application.usecase.auth;

import br.org.bandasantabarbara.application.services.storage.FileService;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.repositories.MembroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.UUID;

@Service
public class ObterFotoPerfilUseCase {
    private final FileService fileService;
    private final MembroRepository membroRepository;

    public ObterFotoPerfilUseCase(
            FileService fileService,
            MembroRepository membroRepository
    ) {
        this.fileService = fileService;
        this.membroRepository = membroRepository;
    }

    @Transactional(readOnly = true)
    public InputStream open(UUID fileId, UUID membroId) {
        var membro = this.membroRepository.findByFileIdWithFile(fileId).orElseThrow(() -> new NotFoundException("Foto perfil não encontrada."));

        if (!membro.getId().equals(membroId)) {
            throw new NotFoundException("Foto perfil não encontrada.");
        }

        return this.fileService.open(membro.getAvatarFile());
    }
}
