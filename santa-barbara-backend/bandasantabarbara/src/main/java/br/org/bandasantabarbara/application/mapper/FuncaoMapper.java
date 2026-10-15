package br.org.bandasantabarbara.application.mapper;

import br.org.bandasantabarbara.application.dtos.membros.FuncaoResponse;
import br.org.bandasantabarbara.model.Funcao;
import org.springframework.stereotype.Component;

@Component
public class FuncaoMapper {

    public FuncaoResponse toResponse(Funcao funcao) {
        return new FuncaoResponse(
                funcao.getCode(),
                funcao.getNome(),
                funcao.getDescricao()
        );
    }
}
