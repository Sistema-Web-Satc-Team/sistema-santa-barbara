package br.org.bandasantabarbara.application.usecase.membros;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.membros.MembroResponse;
import br.org.bandasantabarbara.application.mapper.MembroMapper;
import br.org.bandasantabarbara.application.mapper.PageMapper;
import br.org.bandasantabarbara.repositories.MembroRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ConsultarMembrosUseCase {

    private final MembroRepository membroRepository;
    private final MembroMapper membroMapper;
    private final PageMapper pageMapper;

    public ConsultarMembrosUseCase(
            MembroRepository membroRepository,
            MembroMapper membroMapper,
            PageMapper pageMapper
    ) {
        this.membroRepository = membroRepository;
        this.membroMapper = membroMapper;
        this.pageMapper = pageMapper;
    }


    public PageResponse<MembroResponse> listarTudo(Pageable pageable) {
        Page<MembroResponse> page = membroRepository
                .findAll(pageable)
                .map(membroMapper::toResponse);

        return pageMapper.toResponse(page);
    }
    public PageResponse<MembroResponse> buscarPorNomeOuEmail(
            Pageable pageable,
            String nome,
            String email
    ) {
        Page<MembroResponse> page = membroRepository
                .findByNomeOrEmail(nome, email, pageable)
                .map(membroMapper::toResponse);

        return pageMapper.toResponse(page);
    }



}