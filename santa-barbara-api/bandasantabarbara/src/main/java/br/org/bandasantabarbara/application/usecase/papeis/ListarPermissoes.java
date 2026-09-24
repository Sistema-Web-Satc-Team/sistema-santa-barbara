package br.org.bandasantabarbara.application.usecase.papeis;

import br.org.bandasantabarbara.application.dtos.OffsetPaginationRequest;
import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.model.Permissao;
import br.org.bandasantabarbara.repositories.PermissaoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarPermissoes {

    private final PermissaoRepository permissaoRepository;

    public ListarPermissoes(PermissaoRepository permissaoRepository) {
        this.permissaoRepository = permissaoRepository;
    }

    public PageResponse<String> executar(OffsetPaginationRequest dto){
        Pageable pageable = PageRequest.of(
                dto.page(),
                dto.size(),
                Sort.by("criadoEm").descending()
        );

        List<Permissao> permissoes = this.permissaoRepository.listar(pageable);

        List<String> nomes = permissoes.stream()
                .map(Permissao::getNome)
                .toList();

        return new PageResponse<>(
                nomes,
                dto.page(),
                dto.size()
        );
    }
}
