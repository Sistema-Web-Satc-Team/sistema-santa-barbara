package br.org.bandasantabarbara.infrastructure.controller;

import br.org.bandasantabarbara.application.dtos.OffsetPaginationRequest;
import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.usecase.AdicionarPapeis;
import br.org.bandasantabarbara.application.usecase.AtribuirPermissoesParaPapel;
import br.org.bandasantabarbara.application.usecase.ListarPermissoes;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/permissoes")
public class PermissaoController {

    private final ListarPermissoes listarPermissoesUsecase;

    public PermissaoController(
            ListarPermissoes listarPermissoesUsecase
    ) {
        this.listarPermissoesUsecase = listarPermissoesUsecase;
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('PERMISSAO:LISTAR')")
    public PageResponse<String> listarPermissoes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var dto = new OffsetPaginationRequest(page, size);
        return this.listarPermissoesUsecase.executar(dto);
    }

}
