package br.org.bandasantabarbara.application.usecase.auth;

import br.org.bandasantabarbara.application.services.storage.FileService;
import br.org.bandasantabarbara.repositories.MembroRepository;
import br.org.bandasantabarbara.repositories.StoredFileRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
        var avatar = this.fileService.upload(file);
        var membro = this.membroRepository.findById(membroId).orElseThrow(() -> new RuntimeException("Ocorreu um erro interno"));
        membro.setAvatarFile(avatar);
    }
}
