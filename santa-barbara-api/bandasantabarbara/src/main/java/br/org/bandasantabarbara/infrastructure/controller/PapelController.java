package br.org.bandasantabarbara.infrastructure.controller;

import br.org.bandasantabarbara.application.dtos.DefaultMessageResponse;
import br.org.bandasantabarbara.application.dtos.OffsetPaginationRequest;
import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.papel.AdicionarPapelRequest;
import br.org.bandasantabarbara.application.dtos.papel.AtribuirPermissaoRequest;
import br.org.bandasantabarbara.application.dtos.papel.ExcluirPapelRequest;
import br.org.bandasantabarbara.application.usecase.papeis.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/papeis")
public class PapelController {

    private final AdicionarPapeis adicionarPapeisUsecase;
    private final AtribuirPermissoesParaPapel atribuirPermissaoUsecase;
    private final ListarPapeis listarPapeisUsecase;
    private final ExcluirPapel excluirPapelUsecase;
    private final ListarPermissoesDoPapel listarPermissoesDoPapelUsecase;

    public PapelController(
            AdicionarPapeis adicionarPapeisUsecase,
            AtribuirPermissoesParaPapel atribuirPermissaoUsecase,
            ListarPapeis listarPapeisUsecase,
            ExcluirPapel excluirPapelUsecase,
            ListarPermissoesDoPapel listarPermissoesDoPapelUsecase
    ) {
            this.adicionarPapeisUsecase = adicionarPapeisUsecase;
            this.atribuirPermissaoUsecase = atribuirPermissaoUsecase;
            this.listarPapeisUsecase = listarPapeisUsecase;
            this.excluirPapelUsecase = excluirPapelUsecase;
            this.listarPermissoesDoPapelUsecase = listarPermissoesDoPapelUsecase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('PAPEL:ADICIONAR')")
    public DefaultMessageResponse adicionarPapel(@RequestBody AdicionarPapelRequest request) {
        this.adicionarPapeisUsecase.executar(request);

        return new DefaultMessageResponse("Papel adicionado com sucesso");
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('PAPEL:ATRIBUIR_PERMISSAO')")
    public DefaultMessageResponse atribuirPermissoes(@RequestBody AtribuirPermissaoRequest request) {
        this.atribuirPermissaoUsecase.executar(request);

        return new DefaultMessageResponse("Permissões atribuidas com sucesso.");
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('PAPEL:VISUALIZAR')")
    public PageResponse<String> listarPapeis(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var dto = new OffsetPaginationRequest(page, size);
        return this.listarPapeisUsecase.executar(dto);
    }


    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('PAPEL:REMOVER')")
    public void removerPapel(
            @RequestBody ExcluirPapelRequest request
    ) {
        this.excluirPapelUsecase.executar(request);
    }


    @GetMapping("/{nome}/permissoes")
    @PreAuthorize("""
        hasAuthority('PAPEL:VISUALIZAR')
        and hasAuthority('PERMISSAO:LISTAR')
    """)
    public List<String> listarPermissoes(
            @PathVariable String nome
    ) {
        return listarPermissoesDoPapelUsecase.executar(nome);
    }



}
