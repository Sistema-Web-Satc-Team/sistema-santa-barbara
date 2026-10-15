package br.org.bandasantabarbara.application.usecase.membros;

import br.org.bandasantabarbara.application.dtos.membros.responsaveis.MembroResponsaveisResponse;
import br.org.bandasantabarbara.application.mapper.MembroMapper;
import br.org.bandasantabarbara.application.mapper.ResponsavelMapper;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.model.MembroResponsavel;
import br.org.bandasantabarbara.repositories.MembroRepository;
import br.org.bandasantabarbara.repositories.MembroResponsavelRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ConsultarResponsaveisDependente {

    private final MembroRepository membroRepository;
    private final MembroResponsavelRepository membroResponsavelRepository;
    private final ResponsavelMapper responsavelMapper;
    private final MembroMapper membroMapper;

    public ConsultarResponsaveisDependente(
            MembroRepository membroRepository,
            MembroResponsavelRepository membroResponsavelRepository,
            ResponsavelMapper responsavelMapper,
            MembroMapper membroMapper
    ) {
        this.membroRepository = membroRepository;
        this.membroResponsavelRepository = membroResponsavelRepository;
        this.membroMapper = membroMapper;
        this.responsavelMapper = responsavelMapper;
    }


    public MembroResponsaveisResponse execute(UUID membroId) {
        Membro dependente = this.membroRepository.findById(membroId).orElseThrow(() -> new NotFoundException("Membro informado não encontrado."));

        List<MembroResponsavel> responsaveis = this.membroResponsavelRepository.findByMembroMenorId(membroId);


        return new MembroResponsaveisResponse(
                this.membroMapper.toResponse(dependente),
                responsaveis.stream().map(this.responsavelMapper::toResponse).toList()
        );
    }

}
