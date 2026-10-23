package br.org.bandasantabarbara.application.usecase.auth;


import br.org.bandasantabarbara.application.dtos.profile.PerfilRequest;
import br.org.bandasantabarbara.application.dtos.profile.PerfilResponse;
import br.org.bandasantabarbara.application.mapper.PerfilMapper;
import br.org.bandasantabarbara.application.services.storage.FileService;
import br.org.bandasantabarbara.model.Funcao;
import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.repositories.FuncaoRepository;
import br.org.bandasantabarbara.repositories.MembroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
