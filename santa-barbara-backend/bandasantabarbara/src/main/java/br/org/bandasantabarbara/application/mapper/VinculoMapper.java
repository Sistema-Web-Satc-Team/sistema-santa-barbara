package br.org.bandasantabarbara.application.mapper;

import br.org.bandasantabarbara.application.dtos.membros.vinculos.VinculoResponse;
import br.org.bandasantabarbara.model.MembroVinculo;
import org.springframework.stereotype.Component;


@Component
public class VinculoMapper {

    private final FuncaoMapper funcaoMapper;

    public VinculoMapper(FuncaoMapper funcaoMapper) {
        this.funcaoMapper = funcaoMapper;
    }

    public VinculoResponse toResponse(MembroVinculo vinculo) {
        return new VinculoResponse(
          vinculo.getVinculoId(),
          vinculo.getDataInicio(),
          vinculo.getDataTermino(),
          this.funcaoMapper.toResponse(vinculo.getFuncao()),
          vinculo.getStatus()
        );
    }

}
