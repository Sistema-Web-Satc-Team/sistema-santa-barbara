package br.org.bandasantabarbara.application.services.membro;


import br.org.bandasantabarbara.application.dtos.profile.PerfilRequest;
import br.org.bandasantabarbara.application.dtos.profile.PerfilResponse;
import br.org.bandasantabarbara.application.mapper.PerfilMapper;
import br.org.bandasantabarbara.application.services.storage.FileService;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.membro.Funcao;
import br.org.bandasantabarbara.model.membro.Membro;
import br.org.bandasantabarbara.model.storage.StoredFile;
import br.org.bandasantabarbara.repositories.membro.FuncaoRepository;
import br.org.bandasantabarbara.repositories.membro.MembroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
public class PerfilService {

    private final MembroRepository membroRepository;
    private final PerfilMapper perfilMapper;
    private final FileService fileService;

    private final FuncaoRepository funcaoRepository;

    public PerfilService(MembroRepository membroRepository, PerfilMapper perfilMapper, FileService fileService, FuncaoRepository funcaoRepository) {
        this.membroRepository = membroRepository;
        this.perfilMapper = perfilMapper;
        this.fileService = fileService;
        this.funcaoRepository = funcaoRepository;
    }


    public PerfilResponse buscarComClaims(UUID membroId, String nomeUsuario, List<String> papeis) {
        Membro membro = this.membroRepository.findById(membroId).orElseThrow(() -> new RuntimeException("Ocorreu um erro interno no servidor."));

        List<Funcao> funcoes = this.funcaoRepository.findByCodeIn(papeis);

        return this.perfilMapper.toResponse(membro, nomeUsuario, funcoes, this.fileService.urlForByResource( "/auth/me",membro.getAvatarFile()));
    }

    @Transactional
    public void atualizar(UUID membroId, PerfilRequest request) {
        Membro membro = this.membroRepository.findById(membroId).orElseThrow(() -> new RuntimeException("Ocorreu um erro interno no servidor."));

        this.perfilMapper.toUpdate(membro, request);
    }


    @Transactional(readOnly = true)
    public InputStream verFotoPerfil(UUID fileId, UUID membroId) {
        var membro = this.membroRepository
            .findByFileIdWithFile(fileId)
            .orElseThrow(
                () -> new NotFoundException("Foto perfil não encontrada.")
            );

        if (!membro.getId().equals(membroId)) {
            throw new NotFoundException("Foto perfil não encontrada.");
        }

        return this.fileService.open(membro.getAvatarFile());
    }

    @Transactional
    public void adicionarFotoPerfil(UUID membroId, MultipartFile file) {
        var membro = membroRepository.findById(membroId)
                .orElseThrow(
                    () -> new RuntimeException("Ocorreu um erro interno.")
                );

        StoredFile antigo = membro.getAvatarFile();
        StoredFile novo = fileService.upload(file);

        membro.setAvatarFile(novo);
        membroRepository.flush();

        if (antigo != null) {
            fileService.delete(antigo.getId());
        }
    }


    @Transactional
    public void removerFotoPerfil(UUID membroId) {
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
