package br.org.bandasantabarbara.application.usecase.auth;


import br.org.bandasantabarbara.application.dtos.profile.PerfilRequest;
import br.org.bandasantabarbara.application.dtos.profile.PerfilResponse;
import br.org.bandasantabarbara.application.mapper.PerfilMapper;
import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.repositories.MembroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PerfilService {

    private final MembroRepository membroRepository;
    private final PerfilMapper perfilMapper;

    public PerfilService(MembroRepository membroRepository, PerfilMapper perfilMapper) {
        this.membroRepository = membroRepository;
        this.perfilMapper = perfilMapper;
    }


    public PerfilResponse buscarComClaims(UUID membroId, String nomeUsuario, List<String> papeis) {

        Membro membro = this.membroRepository.findById(membroId).orElseThrow(() -> new RuntimeException("Ocorreu um erro interno no servidor."));

        // TO-DO pegar a url do avatar.

        return this.perfilMapper.toResponse(membro, nomeUsuario, papeis, "");
    }

    @Transactional
    public void atualizar(UUID membroId, PerfilRequest request) {
        Membro membro = this.membroRepository.findById(membroId).orElseThrow(() -> new RuntimeException("Ocorreu um erro interno no servidor."));

        this.perfilMapper.toUpdate(membro, request);
    }
}
