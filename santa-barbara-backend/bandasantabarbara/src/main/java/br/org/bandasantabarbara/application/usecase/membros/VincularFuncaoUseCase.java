package br.org.bandasantabarbara.application.usecase.membros;

import br.org.bandasantabarbara.application.dtos.membros.FuncaoResponse;
import br.org.bandasantabarbara.application.dtos.membros.vinculos.VincularMembroRequest;
import br.org.bandasantabarbara.application.dtos.membros.vinculos.VinculoResponse;
import br.org.bandasantabarbara.application.dtos.membros.vinculos.VinculosResponse;
import br.org.bandasantabarbara.application.mapper.VinculoMapper;
import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.exception.ConflictException;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.Funcao;
import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.model.MembroResponsavel;
import br.org.bandasantabarbara.model.MembroVinculo;
import br.org.bandasantabarbara.repositories.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class VincularFuncaoUseCase {

    private final MembroRepository membroRepository;
    private final MembroVinculoRepository membroVinculoRepository;
    private final MembroResponsavelRepository membroResponsavelRepository;
    private final FuncaoRepository funcaoRepository;
    private final VinculoMapper vinculoMapper;

    public VincularFuncaoUseCase(
            MembroRepository membroRepository,
            MembroVinculoRepository membroVinculoRepository,
            FuncaoRepository funcaoRepository,
            MembroResponsavelRepository membroResponsavelRepository,
            VinculoMapper vinculoMapper
    ) {
        this.membroRepository = membroRepository;
        this.membroVinculoRepository = membroVinculoRepository;
        this.funcaoRepository = funcaoRepository;
        this.vinculoMapper = vinculoMapper;
        this.membroResponsavelRepository = membroResponsavelRepository;
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


        if (codigoNovoVinculo.equals("RESPONSAVEL")) {
            boolean possuiResponsabilidade =
                    membroResponsavelRepository
                            .existsByResponsavel_Id(membroId);

            if (!possuiResponsabilidade) {
                throw new ConflictException(
                        "A função RESPONSAVEL exige que o membro seja responsável por pelo menos uma pessoa."
                );
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
