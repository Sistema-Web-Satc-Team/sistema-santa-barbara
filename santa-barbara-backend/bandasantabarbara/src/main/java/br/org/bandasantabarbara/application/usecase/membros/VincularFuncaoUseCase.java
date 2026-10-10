package br.org.bandasantabarbara.application.usecase.membros;

import br.org.bandasantabarbara.application.dtos.membros.FuncaoResponse;
import br.org.bandasantabarbara.application.dtos.membros.vinculos.VincularMembroRequest;
import br.org.bandasantabarbara.application.dtos.membros.vinculos.VinculoResponse;
import br.org.bandasantabarbara.application.dtos.membros.vinculos.VinculosResponse;
import br.org.bandasantabarbara.application.mapper.VinculoMapper;
import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.Funcao;
import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.model.MembroVinculo;
import br.org.bandasantabarbara.repositories.FuncaoRepository;
import br.org.bandasantabarbara.repositories.MembroRepository;
import br.org.bandasantabarbara.repositories.MembroSpecs;
import br.org.bandasantabarbara.repositories.MembroVinculoRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class VincularFuncaoUseCase {

    private final MembroRepository membroRepository;
    private final MembroVinculoRepository membroVinculoRepository;
    private final FuncaoRepository funcaoRepository;
    private final VinculoMapper vinculoMapper;

    public VincularFuncaoUseCase(
            MembroRepository membroRepository,
            MembroVinculoRepository membroVinculoRepository,
            FuncaoRepository funcaoRepository,
            VinculoMapper vinculoMapper
    ) {
        this.membroRepository = membroRepository;
        this.membroVinculoRepository = membroVinculoRepository;
        this.funcaoRepository = funcaoRepository;
        this.vinculoMapper = vinculoMapper;
    }


    public VinculoResponse execute(
            VincularMembroRequest request,
            UUID membroId
    ) {


        Membro membro = this.membroRepository.findByIdWithVinculos(
                membroId
        ).orElseThrow(
                ( ) -> new NotFoundException("Membro não encontrado.")
        );

        Funcao novaFuncao = this.funcaoRepository
            .findFuncaoByCodeOrNome(request.funcao())
            .orElseThrow(
                () -> new NotFoundException("Função inexistente no sistema. Verifique a lista de funções disponiveis!")
            );


        var codigoNovoVinculo = novaFuncao.getCode();

        for ( var vinculoAberto : membro.getVinculosAbertos()) {

            var codigoVinculoAberto = vinculoAberto.getFuncao().getCode();



            if (codigoVinculoAberto.equals(codigoNovoVinculo)) {
                throw new BadRequestException("O membro já tem um vínculo igual ativo ou agendado.");
            }

            boolean adminComAluno =
                    (codigoVinculoAberto.equals("SUPER_ADMIN") && codigoNovoVinculo.equals("ALUNO")) ||
                            (codigoVinculoAberto.equals("ALUNO") && codigoNovoVinculo.equals("SUPER_ADMIN"));

            if (adminComAluno) {
                throw new BadRequestException("Administradores não podem ser alunos.");
            }

        }

        var vinculo = new MembroVinculo(
                membro,
                novaFuncao,
                request.dataInicio()
        );


        this.membroVinculoRepository.save(vinculo);

        return this.vinculoMapper.toResponse(vinculo);
    }
}
