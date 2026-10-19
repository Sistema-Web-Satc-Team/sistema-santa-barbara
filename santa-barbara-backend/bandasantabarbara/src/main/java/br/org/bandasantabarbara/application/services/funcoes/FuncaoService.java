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

    public List<FuncaoResponse> listarFuncoes(String atorPapel) {

        // DEBITO-04
        // Violação do OCP
        // Violação do SRP
        // Dispersão de Regras
        return this.funcaoRepository.findAll().stream().filter(
                (funcao) -> {
                    if (atorPapel.equals("SECRETARIO")) {
                        return funcao.getTipo() == Funcao.TipoFuncaoEnum.DOMINIO;
                    }

                    if (atorPapel.equals("ADMIN")) {
                        return !funcao.getCode().equals("SUPER_ADMIN");
                    }

                    return true;
                }
        ).map(funcaoMapper::toResponse).toList();
    }

}
