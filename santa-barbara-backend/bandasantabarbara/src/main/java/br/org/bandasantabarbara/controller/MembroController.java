package br.org.bandasantabarbara.controller;

import br.org.bandasantabarbara.application.dtos.*;
import br.org.bandasantabarbara.application.dtos.membros.*;
import br.org.bandasantabarbara.application.usecase.membros.AtualizarDadosMembroUseCase;
import br.org.bandasantabarbara.application.usecase.membros.CadastrarMembroUseCase;
import br.org.bandasantabarbara.application.usecase.membros.ConsultarMembrosUseCase;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/membros")
public class MembroController {

    private final CadastrarMembroUseCase cadastrarMembroUseCase;
    private final ConsultarMembrosUseCase consultarMembrosUseCase;
    private final AtualizarDadosMembroUseCase atualizarDadosMembroUseCase;

    public MembroController(
            CadastrarMembroUseCase cadastrarMembroUseCase,
            ConsultarMembrosUseCase consultarMembrosUseCase,
            AtualizarDadosMembroUseCase atualizarDadosMembroUseCase
    ) {
        this.cadastrarMembroUseCase = cadastrarMembroUseCase;
        this.consultarMembrosUseCase = consultarMembrosUseCase;
        this.atualizarDadosMembroUseCase = atualizarDadosMembroUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public MessageResponse registrarMembro(
            @RequestBody @Valid MembroRequest dto
    ) {
        cadastrarMembroUseCase.execute(dto);

        return new MessageResponse("Membro adicionado com sucesso.");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public PageResponse<MembroResponse> listarMembros(
            @PageableDefault(
                    size = 20,
                    sort = "nome",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable,
            @RequestParam(required = false) String search
    ) {

        if (search != null && !search.isBlank()) {
            return  consultarMembrosUseCase.buscarPorNomeOuEmail(pageable, search, search);
        }

        return consultarMembrosUseCase.listarTudo(pageable);
    }


    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public void atualizarMembro(
            @PathVariable UUID id,
            @RequestBody @Valid AtualizarMembroRequest dto
    ) {
        atualizarDadosMembroUseCase.execute(id, dto);
    }
}