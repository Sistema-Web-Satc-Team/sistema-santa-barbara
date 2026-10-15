package br.org.bandasantabarbara.application.usecase.membros;

import br.org.bandasantabarbara.application.dtos.membros.responsaveis.MembroRelacaoRequest;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.model.MembroResponsavel;
import br.org.bandasantabarbara.repositories.MembroRepository;
import br.org.bandasantabarbara.repositories.MembroResponsavelRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AdicionarResponsavelUseCase {

    private final MembroResponsavelRepository membroResponsavelRepository;
    private final MembroRepository membroRepository;

    public AdicionarResponsavelUseCase(MembroResponsavelRepository membroResponsavelRepository, MembroRepository membroRepository) {
        this.membroResponsavelRepository = membroResponsavelRepository;
        this.membroRepository = membroRepository;
    }


    public void execute(UUID idResponsavel, UUID idMembro, MembroRelacaoRequest request) {
        Membro responsavel = this.membroRepository.findById(idResponsavel).orElseThrow(() -> new NotFoundException("Responsável informado não encontrado"));

        Membro membroDependente = this.membroRepository.findById(idMembro).orElseThrow(() -> new NotFoundException("Membro dependente informado não encontrado"));

        if (this.membroResponsavelRepository.existsByResponsavel_IdAndMembroMenor_Id(idResponsavel, idMembro) ) {
            return;
        }

        var relacao = new MembroResponsavel();

        relacao.setResponsavel(responsavel);
        relacao.setMembroMenor(membroDependente);
        relacao.setTipoRelacao(request.grauRelacao());

        this.membroResponsavelRepository.save(relacao);
    }

}
