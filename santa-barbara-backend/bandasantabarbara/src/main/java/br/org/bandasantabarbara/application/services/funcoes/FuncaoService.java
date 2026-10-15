package br.org.bandasantabarbara.application.services.funcoes;


import br.org.bandasantabarbara.application.dtos.membros.FuncaoResponse;
import br.org.bandasantabarbara.application.mapper.FuncaoMapper;
import br.org.bandasantabarbara.model.Funcao;
import br.org.bandasantabarbara.repositories.FuncaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuncaoService {

    private final FuncaoRepository funcaoRepository;
    private final FuncaoMapper funcaoMapper;

    public FuncaoService(FuncaoRepository funcaoRepository, FuncaoMapper funcaoMapper) {
        this.funcaoRepository = funcaoRepository;
        this.funcaoMapper = funcaoMapper;
    }

    public List<FuncaoResponse> listarFuncoes() {
        return this.funcaoRepository.findAll().stream().map(funcaoMapper::toResponse).toList();
    }

}
