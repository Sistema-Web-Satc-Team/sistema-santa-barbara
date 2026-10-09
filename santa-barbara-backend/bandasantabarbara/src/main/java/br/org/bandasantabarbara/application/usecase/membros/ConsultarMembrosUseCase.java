package br.org.bandasantabarbara.application.usecase.membros;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.membros.MembroResponse;
import br.org.bandasantabarbara.application.mapper.MembroMapper;
import br.org.bandasantabarbara.application.mapper.PageMapper;
import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.repositories.MembroRepository;
import br.org.bandasantabarbara.repositories.MembroSpecs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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


    public PageResponse<MembroResponse> listarMembros(String busca, Pageable pageable) {
        Specification<Membro> spec = Specification.where(MembroSpecs.porNomeOuEmail(busca))
                .and(MembroSpecs.comFiltroVinculos(true, true));

        Page<Membro> membrosPage = membroRepository.findAll(spec, pageable);

        return this.pageMapper.toResponse(membrosPage.map(
                this.membroMapper::toResponse
        ));
    }

}