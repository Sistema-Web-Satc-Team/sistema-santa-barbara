package br.org.bandasantabarbara.application.usecase;

import br.org.bandasantabarbara.application.dtos.OffsetPaginationRequest;
import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.model.Papel;
import br.org.bandasantabarbara.model.Permissao;
import br.org.bandasantabarbara.repositories.PapelRepository;
import br.org.bandasantabarbara.repositories.PermissaoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarPapeis {

    private final PapelRepository papelRepository;

    public ListarPapeis(PapelRepository papelRepository) {
        this.papelRepository = papelRepository;
    }

    public PageResponse<String> executar(OffsetPaginationRequest dto){
        Pageable pageable = PageRequest.of(
                dto.page(),
                dto.size(),
                Sort.by("criadoEm").descending()
        );

        List<Papel> papeis = this.papelRepository.listar(pageable);

        List<String> nomes = papeis.stream()
                .map(Papel::getNome)
                .toList();

        return new PageResponse<>(
                nomes,
                dto.page(),
                dto.size()
        );
    }
}
