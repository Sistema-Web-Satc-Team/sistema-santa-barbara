package br.org.bandasantabarbara.controller;

import br.org.bandasantabarbara.application.dtos.*;
import br.org.bandasantabarbara.application.dtos.membros.*;
import br.org.bandasantabarbara.application.dtos.membros.responsaveis.MembroRelacaoRequest;
import br.org.bandasantabarbara.application.dtos.membros.responsaveis.MembroResponsaveisResponse;
import br.org.bandasantabarbara.application.dtos.membros.vinculos.MembroVinculosResponse;
import br.org.bandasantabarbara.application.dtos.membros.vinculos.VincularMembroRequest;
import br.org.bandasantabarbara.application.dtos.membros.vinculos.VinculoResponse;
import br.org.bandasantabarbara.application.usecase.membros.*;
import br.org.bandasantabarbara.exception.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    private final CancelarVinculoUseCase cancelarVinculoUseCase;

    private final AdicionarResponsavelUseCase adicionarResponsavelUseCase;
    private final RemoverResponsavelUseCase removerResponsavelUseCase;
    private final ConsultarResponsaveisDependente consultarResponsaveisDependente;

    public MembroController(
            CadastrarMembroUseCase cadastrarMembroUseCase,
            ConsultarMembrosUseCase consultarMembrosUseCase,
            AtualizarDadosMembroUseCase atualizarDadosMembroUseCase,
            VincularFuncaoUseCase vincularFuncaoUseCase,
            ConsultarVinculosUseCase consultarVinculosUseCase,
            EncerrarVinculoUseCase encerrarVinculoUseCase,
            CancelarVinculoUseCase cancelarVinculoUseCase,
            AdicionarResponsavelUseCase adicionarResponsavelUseCase,
            RemoverResponsavelUseCase removerResponsavelUseCase,
            ConsultarResponsaveisDependente consultarResponsaveisDependente
    ) {
        this.cadastrarMembroUseCase = cadastrarMembroUseCase;
        this.consultarMembrosUseCase = consultarMembrosUseCase;
        this.atualizarDadosMembroUseCase = atualizarDadosMembroUseCase;
        this.vincularFuncaoUseCase = vincularFuncaoUseCase;
        this.consultarVinculosUseCase = consultarVinculosUseCase;
        this.encerrarVinculoUseCase = encerrarVinculoUseCase;
        this.cancelarVinculoUseCase = cancelarVinculoUseCase;
        this.adicionarResponsavelUseCase = adicionarResponsavelUseCase;
        this.removerResponsavelUseCase = removerResponsavelUseCase;
        this.consultarResponsaveisDependente = consultarResponsaveisDependente;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO')")
    public MembroResponse registrarMembro(
            @RequestBody @Valid MembroRequest dto
    ) {
        return cadastrarMembroUseCase.execute(dto);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO')")
    public PageResponse<MembroResponse> listarMembros(
            @PageableDefault(
                    size = 20,
                    sort = "nome",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean maiorDeIdade,
            @RequestParam(required = false) Boolean semAcesso
    ) {
        return consultarMembrosUseCase.listarMembros(search, maiorDeIdade, semAcesso, pageable);
    }


    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO')")
    public void atualizarMembro(
            @PathVariable UUID id,
            @RequestBody @Valid MembroRequest dto
    ) {
        atualizarDadosMembroUseCase.execute(id, dto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO')")
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
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO')")
    public MembroVinculosResponse listarVinculosMembro(
            @PathVariable UUID id
    ) {
        return this.consultarVinculosUseCase.execute(id);
    }


    @PostMapping("/{id}/vinculos")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO')")
    public VinculoResponse criarVinculoMembro(
            @RequestBody @Valid VincularMembroRequest dto,
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        List<String> papeis = jwt.getClaim("papeis");

        List<String> hierarquia = List.of("SUPER_ADMIN", "ADMIN", "SECRETARIO");

        String papelMaisForte = hierarquia.stream()
                .filter(papeis::contains)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Nenhum papel válido encontrado"));

        return vincularFuncaoUseCase.execute(dto, id, papelMaisForte);
    }


    @PostMapping("/{idMembro}/vinculos/{idVinculo}/encerrar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO')")
    public void encerrarVinculoMembro(
            @PathVariable UUID idMembro,
            @PathVariable int idVinculo,
            @AuthenticationPrincipal Jwt jwt
    ) {
        List<String> papeis = jwt.getClaim("papeis");

        List<String> hierarquia = List.of("SUPER_ADMIN", "ADMIN", "SECRETARIO");

        String papelMaisForte = hierarquia.stream()
                .filter(papeis::contains)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Nenhum papel válido encontrado"));


        this.encerrarVinculoUseCase.execute(idMembro, idVinculo, papelMaisForte);
    }

    @DeleteMapping("/{idMembro}/vinculos/{idVinculo}/cancelar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO')")
    public void cancelarVinculoMembro(
            @PathVariable UUID idMembro,
            @PathVariable int idVinculo,
            @AuthenticationPrincipal Jwt jwt
    ) {
        List<String> papeis = jwt.getClaim("papeis");

        List<String> hierarquia = List.of("SUPER_ADMIN", "ADMIN", "SECRETARIO");

        String papelMaisForte = hierarquia.stream()
                .filter(papeis::contains)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Nenhum papel válido encontrado"));

        this.cancelarVinculoUseCase.cancelar(idMembro, idVinculo, papelMaisForte);
    }

    /*
    *
    * MEMBROS DE MENORES
    *
     */


    @PostMapping("/menores")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO')")
    public MembroResponse criarMembroMenor(
            @RequestBody @Valid RegistrarMembroMenorRequest dto
    ) {
       return this.cadastrarMembroUseCase.cadastrarMenorIdade(dto);
    }

    @PutMapping("/{idMembro}/responsaveis/{idResponsavel}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO')")
    public void atribuirResponsavel(
            @RequestBody @Valid MembroRelacaoRequest dto,
            @PathVariable UUID idMembro,
            @PathVariable UUID idResponsavel
    ) {
        this.adicionarResponsavelUseCase.execute(idResponsavel, idMembro, dto);
    }


    @DeleteMapping("/{idMembro}/responsaveis/{idResponsavel}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO')")
    public void removerResponsavel(
            @PathVariable UUID idMembro,
            @PathVariable UUID idResponsavel
    ) {
       this.removerResponsavelUseCase.execute(idResponsavel, idMembro);
    }


    @GetMapping("/{idMembro}/responsaveis")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO')")
    public MembroResponsaveisResponse listarResponsaveisDependente(
            @PathVariable UUID idMembro
    ) {
        return this.consultarResponsaveisDependente.execute(idMembro);
    }


}