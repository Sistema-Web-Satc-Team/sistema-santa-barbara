package br.org.bandasantabarbara.controller;

import br.org.bandasantabarbara.application.dtos.*;
import br.org.bandasantabarbara.application.dtos.membros.*;
import br.org.bandasantabarbara.application.dtos.membros.vinculos.MembroVinculosResponse;
import br.org.bandasantabarbara.application.dtos.membros.vinculos.VincularMembroRequest;
import br.org.bandasantabarbara.application.dtos.membros.vinculos.VinculoResponse;
import br.org.bandasantabarbara.application.dtos.membros.vinculos.VinculosResponse;
import br.org.bandasantabarbara.application.usecase.membros.AtualizarDadosMembroUseCase;
import br.org.bandasantabarbara.application.usecase.membros.CadastrarMembroUseCase;
import br.org.bandasantabarbara.application.usecase.membros.ConsultarMembrosUseCase;
import br.org.bandasantabarbara.application.usecase.membros.VincularFuncaoUseCase;
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

    public MembroController(
            CadastrarMembroUseCase cadastrarMembroUseCase,
            ConsultarMembrosUseCase consultarMembrosUseCase,
            AtualizarDadosMembroUseCase atualizarDadosMembroUseCase,
            VincularFuncaoUseCase vincularFuncaoUseCase
    ) {
        this.cadastrarMembroUseCase = cadastrarMembroUseCase;
        this.consultarMembrosUseCase = consultarMembrosUseCase;
        this.atualizarDadosMembroUseCase = atualizarDadosMembroUseCase;
        this.vincularFuncaoUseCase = vincularFuncaoUseCase;
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

        // Must to be implemented

        return new MembroVinculosResponse(
                new MembroResponse(
                        null,
                        "teste",
                        "48999999999",
                        "Rua teste, 123",
                        "teste@gmail.com",
                        LocalDate.now().minusYears(18),
                        Membro.MembroSexoEnum.MASCULINO
                        ),
                new VinculosResponse(
                        new ArrayList<>(),
                        new ArrayList<>()
                )
        );
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
            @PathVariable UUID idVinculo
    ) {

        // Must to be implemented

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