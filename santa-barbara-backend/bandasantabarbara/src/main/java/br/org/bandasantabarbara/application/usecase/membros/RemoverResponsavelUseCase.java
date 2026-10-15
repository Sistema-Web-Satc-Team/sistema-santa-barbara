package br.org.bandasantabarbara.application.usecase.membros;

import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.model.MembroResponsavel;
import br.org.bandasantabarbara.model.MembroResponsavelId;
import br.org.bandasantabarbara.repositories.MembroRepository;
import br.org.bandasantabarbara.repositories.MembroResponsavelRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RemoverResponsavelUseCase {
    private final MembroResponsavelRepository membroResponsavelRepository;
    private final MembroRepository membroRepository;

    public RemoverResponsavelUseCase(MembroResponsavelRepository membroResponsavelRepository, MembroRepository membroRepository) {
        this.membroResponsavelRepository = membroResponsavelRepository;
        this.membroRepository = membroRepository;
    }

    public void execute(UUID idResponsavel, UUID idMembro) {
        Membro responsavel = this.membroRepository.findById(idResponsavel).orElseThrow(() -> new NotFoundException("Responsável informado não encontrado"));

        Membro membroDependente = this.membroRepository.findById(idMembro).orElseThrow(() -> new NotFoundException("Membro dependente informado não encontrado"));

        if (!this.membroResponsavelRepository.existsByResponsavel_IdAndMembroMenor_Id(idResponsavel, idMembro) ) {
            return;
        }

        if (membroDependente.ehMenorDeIdade()) {
            List<MembroResponsavel> responsaveis = this.membroResponsavelRepository.findByMembroMenorId(membroDependente.getId());

            if (responsaveis.size() <= 1) {
                throw new BadRequestException("Um membro de menor deve ter pelo menos um responsável cadastrado.");
            }

        }

        MembroResponsavelId membroResponsavelId = new MembroResponsavelId();

        membroResponsavelId.setIdMembroMenor(membroDependente.getId());
        membroResponsavelId.setIdResponsavel(responsavel.getId());

        MembroResponsavel membroResponsavel = this.membroResponsavelRepository.findById(membroResponsavelId).orElseThrow(() -> new RuntimeException("Ocorreu um erro interno no servidor."));

        this.membroResponsavelRepository.delete(membroResponsavel);

    }
}
