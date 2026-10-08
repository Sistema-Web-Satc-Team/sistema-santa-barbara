package br.org.bandasantabarbara.application.usecase.membros;

import br.org.bandasantabarbara.application.dtos.membros.FuncaoResponse;
import br.org.bandasantabarbara.application.dtos.membros.vinculos.VincularMembroRequest;
import br.org.bandasantabarbara.application.dtos.membros.vinculos.VinculoResponse;
import br.org.bandasantabarbara.application.dtos.membros.vinculos.VinculosResponse;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.Funcao;
import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.model.MembroVinculo;
import br.org.bandasantabarbara.repositories.FuncaoRepository;
import br.org.bandasantabarbara.repositories.MembroRepository;
import br.org.bandasantabarbara.repositories.MembroVinculoRepository;

import java.util.UUID;

public class VincularFuncaoUseCase {

    private final MembroRepository membroRepository;
    private final MembroVinculoRepository membroVinculoRepository;
    private final FuncaoRepository funcaoRepository;

    public VincularFuncaoUseCase(
            MembroRepository membroRepository,
            MembroVinculoRepository membroVinculoRepository,
            FuncaoRepository funcaoRepository
    ) {
        this.membroRepository = membroRepository;
        this.membroVinculoRepository = membroVinculoRepository;
        this.funcaoRepository = funcaoRepository;
    }


    public VinculoResponse execute(
            VincularMembroRequest request,
            UUID membroId
    ) {

        Membro membro = this.membroRepository.findById(
                membroId
        ).orElseThrow(
                ( ) -> new NotFoundException("Membro não encontrado.")
        );

        Funcao funcao = this.funcaoRepository
            .findFuncaoByCodeOrNome(request.funcao())
            .orElseThrow(
                () -> new NotFoundException("Função inexistente no sistema. Verifique a lista de funções disponiveis!")
            );


        var vinculo = new MembroVinculo(
                membro,
                funcao
        );


        this.membroVinculoRepository.save(vinculo);

        return new VinculoResponse(
                vinculo.getId(),
                vinculo.getDataInicio(),
                vinculo.getDataTermino(),
                new FuncaoResponse(
                        funcao.getCode(),
                        funcao.getNome(),
                        funcao.getDescricao()
                ),
                vinculo.getStatus()
        );
    }
}
