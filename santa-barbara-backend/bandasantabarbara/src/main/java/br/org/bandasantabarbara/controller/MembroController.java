package br.org.bandasantabarbara.controller;

import br.org.bandasantabarbara.application.dtos.*;
import br.org.bandasantabarbara.application.dtos.membros.*;
import br.org.bandasantabarbara.application.dtos.membros.vinculos.MembroVinculosResponse;
import br.org.bandasantabarbara.application.dtos.membros.vinculos.VincularMembroRequest;
import br.org.bandasantabarbara.application.dtos.membros.vinculos.VinculoResponse;
import br.org.bandasantabarbara.application.dtos.membros.vinculos.VinculosResponse;
import br.org.bandasantabarbara.application.usecase.membros.*;
import br.org.bandasantabarbara.model.Membro;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping("/membros")
public class MembroController {

    private final CadastrarMembroUseCase cadastrarMembroUseCase;
    private final ConsultarMembrosUseCase consultarMembrosUseCase;
    private final AtualizarDadosMembroUseCase atualizarDadosMembroUseCase;
    private final VincularFuncaoUseCase vincularFuncaoUseCase;

    private final ConsultarVinculosUseCase consultarVinculosUseCase;
    private final EncerrarVinculoUseCase encerrarVinculoUseCase;

    public MembroController(
            CadastrarMembroUseCase cadastrarMembroUseCase,
            ConsultarMembrosUseCase consultarMembrosUseCase,
            AtualizarDadosMembroUseCase atualizarDadosMembroUseCase,
            VincularFuncaoUseCase vincularFuncaoUseCase,
            ConsultarVinculosUseCase consultarVinculosUseCase,
            EncerrarVinculoUseCase encerrarVinculoUseCase
    ) {
        this.cadastrarMembroUseCase = cadastrarMembroUseCase;
        this.consultarMembrosUseCase = consultarMembrosUseCase;
        this.atualizarDadosMembroUseCase = atualizarDadosMembroUseCase;
        this.vincularFuncaoUseCase = vincularFuncaoUseCase;
        this.consultarVinculosUseCase = consultarVinculosUseCase;
        this.encerrarVinculoUseCase = encerrarVinculoUseCase;
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
        return consultarMembrosUseCase.listarMembros(search, pageable);
    }


    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public void atualizarMembro(
            @PathVariable UUID id,
            @RequestBody @Valid MembroRequest dto
    ) {
        atualizarDadosMembroUseCase.execute(id, dto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public void deletarMembro(
            @PathVariable UUID id
    ) {
        // Must to be implemented
    }

    /*
    *
    * VINCULO DOS MEMBROS
    *
     */

    @GetMapping("/{id}/vinculos")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public MembroVinculosResponse listarVinculosMembro(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean withEncerrados
    ) {
        return this.consultarVinculosUseCase.execute(id);
    }


    @PostMapping("/{id}/vinculos")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public VinculoResponse criarVinculoMembro(
            @RequestBody VincularMembroRequest dto,
            @PathVariable UUID id
    ) {
        return vincularFuncaoUseCase.execute(dto, id);
    }


    @PostMapping("/{idMembro}/vinculos/{idVinculo}/encerrar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public void encerrarVinculoMembro(
            @PathVariable UUID idMembro,
            @PathVariable int idVinculo
    ) {
        this.encerrarVinculoUseCase.execute(idMembro, idVinculo);
    }

    /*
    *
    * MEMBROS DE MENORES
    *
     */


    @PostMapping("/menores")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public MembroResponse criarMembroMenor(
            @RequestBody RegistrarMembroMenorRequest dto
    ) {
       return this.cadastrarMembroUseCase.cadastrarMenorIdade(dto);
    }

    @PutMapping("/{idMembro}/responsaveis/{idResponsavel}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public void atribuirResponsavel(
            @RequestBody MembroRelacaoRequest dto,
            @PathVariable UUID idMembro,
            @PathVariable UUID idResponsavel
    ) {

        // Must to be implemented

    }


    @DeleteMapping("/{idMembro}/responsaveis/{idResponsavel}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public void removerResponsavel(
            @PathVariable UUID idMembro,
            @PathVariable UUID idResponsavel
    ) {

        // Must to be implemented

    }








}