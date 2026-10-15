package br.org.bandasantabarbara.controller;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.alunos.AlunoMock;
import br.org.bandasantabarbara.application.dtos.alunos.AlunoResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/alunos")
public class AlunoController {

    @GetMapping
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public PageResponse<AlunoResponse> listarAlunos(
            @PageableDefault(size = 20, sort = "nome", direction = Sort.Direction.ASC)
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> instrumentosAprendendo,
            @RequestParam(defaultValue = "false") boolean apenasMenores
    ) {
        return AlunoMock.pagina(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                search,
                instrumentosAprendendo,
                apenasMenores
        );
    }

}
