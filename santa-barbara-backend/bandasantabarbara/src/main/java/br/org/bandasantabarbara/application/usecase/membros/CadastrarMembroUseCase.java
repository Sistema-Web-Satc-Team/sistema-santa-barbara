package br.org.bandasantabarbara.application.usecase.membros;

import br.org.bandasantabarbara.application.dtos.membros.MembroRequest;
import br.org.bandasantabarbara.application.dtos.membros.MembroResponse;
import br.org.bandasantabarbara.application.mapper.MembroMapper;
import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.repositories.MembroRepository;
import org.springframework.stereotype.Service;

@Service
public class CadastrarMembroUseCase {

    private final MembroRepository membroRepository;
    private final MembroMapper membroMapper;

    public CadastrarMembroUseCase(MembroRepository membroRepository, MembroMapper membroMapper) {
        this.membroRepository = membroRepository;
        this.membroMapper = membroMapper;
    }

    public MembroResponse execute(MembroRequest request) {
       var membro = this.membroMapper.toEntity(request);
       this.membroRepository.save(membro);
       return  this.membroMapper.toResponse(membro);
    }

}
