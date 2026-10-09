package br.org.bandasantabarbara.application.usecase.membros;

import br.org.bandasantabarbara.application.dtos.membros.MembroRequest;
import br.org.bandasantabarbara.application.dtos.membros.MembroResponse;
import br.org.bandasantabarbara.application.mapper.MembroMapper;
import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.MembroResponsavel;
import br.org.bandasantabarbara.repositories.MembroCredencialRepository;
import br.org.bandasantabarbara.repositories.MembroRepository;
import br.org.bandasantabarbara.repositories.MembroResponsavelRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class AtualizarDadosMembroUseCase {

    private final MembroRepository membroRepository;
    private final MembroCredencialRepository membroCredencialRepository;
    private final MembroResponsavelRepository membroResponsavelRepository;
    private final MembroMapper membroMapper;

    public AtualizarDadosMembroUseCase(
            MembroRepository membroRepository,
            MembroCredencialRepository membroCredencialRepository,
            MembroResponsavelRepository membroResponsavelRepository,
            MembroMapper membroMapper) {
        this.membroRepository = membroRepository;
        this.membroMapper = membroMapper;
        this.membroResponsavelRepository = membroResponsavelRepository;
        this.membroCredencialRepository = membroCredencialRepository;
    }

    public MembroResponse execute(UUID membroId, MembroRequest request) {

        var membro = this.membroRepository.findById(membroId)
                .orElseThrow(
                        () -> new NotFoundException("Membro não encontrado.")
                );


        this.membroCredencialRepository.findByIdWithMembro(membro.getId())
            .ifPresent((credencial) -> {
                if (credencial.isAtivo() && request.email() != null && !request.email().equalsIgnoreCase(membro.getEmail())) {
                    throw new BadRequestException("Email não pode ser alterado com membro ativo.");
                }
        });

        var wasMenorIdade = membro.ehMenorDeIdade();

        this.membroMapper.toUpdate(request, membro);

        if (membro.ehMenorDeIdade() && !wasMenorIdade) {
            var responsaveis = this.membroResponsavelRepository.findByMembroMenorId(membroId);

            if (responsaveis.isEmpty()) {
                throw new BadRequestException("Para o membro se tornar de menor é necessário atribuir pelo menos um responsável antes.");
            }


        }

        this.membroRepository.save(membro);

        return  this.membroMapper.toResponse(membro);
    }
}
