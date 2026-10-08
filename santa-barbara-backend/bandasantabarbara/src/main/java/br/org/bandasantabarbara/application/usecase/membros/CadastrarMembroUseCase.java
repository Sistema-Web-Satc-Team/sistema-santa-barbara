package br.org.bandasantabarbara.application.usecase.membros;

import br.org.bandasantabarbara.application.dtos.membros.MembroRequest;
import br.org.bandasantabarbara.application.dtos.membros.MembroResponse;
import br.org.bandasantabarbara.application.dtos.membros.RegistrarMembroMenorRequest;
import br.org.bandasantabarbara.application.mapper.MembroMapper;
import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.model.MembroResponsavel;
import br.org.bandasantabarbara.repositories.MembroRepository;
import br.org.bandasantabarbara.repositories.MembroResponsavelRepository;
import org.springframework.stereotype.Service;

@Service
public class CadastrarMembroUseCase {

    private final MembroRepository membroRepository;
    private final MembroMapper membroMapper;
    private final MembroResponsavelRepository membroResponsavelRepository;

    public CadastrarMembroUseCase(
            MembroRepository membroRepository,
            MembroMapper membroMapper,
          MembroResponsavelRepository membroResponsavelRepository) {
        this.membroRepository = membroRepository;
        this.membroMapper = membroMapper;
        this.membroResponsavelRepository = membroResponsavelRepository;
    }

    public MembroResponse execute(MembroRequest request) {
       var membro = this.membroMapper.toEntity(request);

       if ( membro.ehMenorDeIdade() ) {
           throw new BadRequestException("Membro cadastrado não pode ser menor de idade.");
       }

       this.membroRepository.save(membro);
       return  this.membroMapper.toResponse(membro);
    }


    public MembroResponse cadastrarMenorIdade(RegistrarMembroMenorRequest request) {

        Membro responsavel = this.membroRepository.findById(request.idResponsavel())
                .orElseThrow(() -> new NotFoundException("Responsável não encontrado.")
                );

        Membro menor = this.membroMapper.toEntityMenor(request.menor());

        if (!menor.ehMenorDeIdade()) {
            throw new BadRequestException("Membro informado não é menor de idade.");
        }

        MembroResponsavel relacao = new MembroResponsavel();

        relacao.setResponsavel(responsavel);
        relacao.setMembroMenor(menor);
        relacao.setTipoRelacao(request.grauRelacao());

        this.membroResponsavelRepository.save(relacao);

        return this.membroMapper.toResponse(menor);
    }

}
