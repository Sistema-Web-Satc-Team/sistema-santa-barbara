package br.org.bandasantabarbara.infrastructure.controller;

import br.org.bandasantabarbara.application.dtos.*;
import br.org.bandasantabarbara.application.dtos.membros.AtualizarMembroParcialmenteRequest;
import br.org.bandasantabarbara.application.dtos.membros.ListMembroRequest;
import br.org.bandasantabarbara.application.dtos.membros.MembroResponse;
import br.org.bandasantabarbara.application.dtos.membros.RegistrarMembroRequest;
import br.org.bandasantabarbara.application.filters.MembroFilter;
import br.org.bandasantabarbara.application.usecase.membros.MembroUsecase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/membros")
public class MembroController {

    private final MembroUsecase usecase;

    public MembroController(MembroUsecase usecase) {
        this.usecase = usecase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('MEMBRO:CADASTRAR')")
    public DefaultMessageResponse registrarMembro(@RequestBody @Valid RegistrarMembroRequest dto) {
        usecase.registrarMembro(dto);
        return new DefaultMessageResponse("Membro adicionado com sucesso.");
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('MEMBRO:VISUALIZAR')")
    public PageResponse<MembroResponse> listarMembros(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false)
            List<String> papel,
            String nome
    ) {
        var membroFilter = new MembroFilter();

        if (papel != null && !papel.isEmpty()) {
            membroFilter.setPapeis(papel);
        }

        if (nome != null && !nome.isBlank()) {
            membroFilter.setNome(nome);
        }


        var offsetPaginationRequest = new OffsetPaginationRequest(page, size);

        var dto = new ListMembroRequest(offsetPaginationRequest, membroFilter);

        return usecase.listarMembros(dto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('MEMBRO:ATUALIZAR')")
    public void atualizarMembro(
            @PathVariable UUID id,
            @RequestBody @Valid AtualizarMembroParcialmenteRequest dto
    ) {
        usecase.atualizarMembro(id, dto);
    }


}
