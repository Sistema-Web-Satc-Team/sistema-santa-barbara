package br.org.bandasantabarbara.controller;

import br.org.bandasantabarbara.application.dtos.*;
import br.org.bandasantabarbara.application.dtos.membros.*;
import br.org.bandasantabarbara.application.dtos.membros.responsaveis.MembroRelacaoRequest;
import br.org.bandasantabarbara.application.dtos.membros.responsaveis.MembroResponsaveisResponse;
import br.org.bandasantabarbara.application.dtos.membros.vinculos.MembroVinculosResponse;
import br.org.bandasantabarbara.application.dtos.membros.vinculos.VincularMembroRequest;
import br.org.bandasantabarbara.application.dtos.membros.vinculos.VinculoResponse;
import br.org.bandasantabarbara.application.services.membro.MembroService;
import br.org.bandasantabarbara.application.services.membro.MembroVinculoService;
import br.org.bandasantabarbara.exception.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Membros", description = "Cadastro e administração de membros, responsáveis e vínculos.")
public class MembroController {

    private final MembroService membroService;
    private final MembroVinculoService membroVinculoService;

    public MembroController(
            MembroService membroService,
            MembroVinculoService membroVinculoService
    ) {
        this.membroService = membroService;
        this.membroVinculoService = membroVinculoService;
    }

    @PostMapping
    @Operation(summary = "Cadastrar membro", description = "Cadastra um membro maior de idade.")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO')")
    public MembroResponse registrarMembro(
            @RequestBody @Valid MembroRequest dto
    ) {
        return membroService.cadastrarMaiorIdade(dto);
    }

    @GetMapping
    @Operation(summary = "Listar membros", description = "Lista membros paginados com filtros opcionais.")
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
        return membroService.listarMembros(search, maiorDeIdade, semAcesso, pageable);
    }


    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar membro", description = "Atualiza os dados de um membro.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO')")
    public void atualizarMembro(
            @PathVariable UUID id,
            @RequestBody @Valid MembroRequest dto
    ) {
        membroService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover membro", description = "Remove um membro sem vínculos e sem credencial associada.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO')")
    public void deletarMembro(
            @PathVariable UUID id
    ) {
        membroService.remover(id);
    }

    /*
    *
    * VINCULO DOS MEMBROS
    *
     */

    @GetMapping("/{id}/vinculos")
    @Operation(summary = "Listar vínculos do membro", description = "Consulta vínculos ativos, agendados, encerrados e cancelados.")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO')")
    public MembroVinculosResponse listarVinculosMembro(
            @PathVariable UUID id
    ) {
        return this.membroVinculoService.pegarVinculosDeUmMembroPorId(id);
    }


    @PostMapping("/{id}/vinculos")
    @Operation(summary = "Criar vínculo", description = "Vincula uma função ao membro.")
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

        return membroVinculoService.vincularFuncao(dto, id, papelMaisForte);
    }


    @PostMapping("/{idMembro}/vinculos/{idVinculo}/encerrar")
    @Operation(summary = "Encerrar vínculo", description = "Encerra um vínculo vigente do membro.")
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


        this.membroVinculoService.encerrar(idMembro, idVinculo, papelMaisForte);
    }

    @DeleteMapping("/{idMembro}/vinculos/{idVinculo}/cancelar")
    @Operation(summary = "Cancelar vínculo agendado", description = "Cancela um vínculo agendado do membro.")
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

        this.membroVinculoService.cancelar(idMembro, idVinculo, papelMaisForte);
    }

    /*
    *
    * MEMBROS DE MENORES
    *
     */


    @PostMapping("/menores")
    @Operation(summary = "Cadastrar membro menor de idade", description = "Cadastra um menor e associa o responsável informado.")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO')")
    public MembroResponse criarMembroMenor(
            @RequestBody @Valid RegistrarMembroMenorRequest dto
    ) {
       return this.membroService.cadastrarMenorIdade(dto);
    }

    @PutMapping("/{idMembro}/responsaveis/{idResponsavel}")
    @Operation(summary = "Atribuir responsável", description = "Associa um responsável ao membro dependente.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO')")
    public void atribuirResponsavel(
            @RequestBody @Valid MembroRelacaoRequest dto,
            @PathVariable UUID idMembro,
            @PathVariable UUID idResponsavel
    ) {
        this.membroService.adicionarResponsavelAoDependente(idResponsavel, idMembro, dto);
    }


    @DeleteMapping("/{idMembro}/responsaveis/{idResponsavel}")
    @Operation(summary = "Remover responsável", description = "Remove a associação do responsável com o membro dependente.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO')")
    public void removerResponsavel(
            @PathVariable UUID idMembro,
            @PathVariable UUID idResponsavel
    ) {
       this.membroService.removerResponsavelDoMembro(idResponsavel, idMembro);
    }


    @GetMapping("/{idMembro}/responsaveis")
    @Operation(summary = "Listar responsáveis", description = "Consulta os responsáveis associados ao membro dependente.")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO')")
    public MembroResponsaveisResponse listarResponsaveisDependente(
            @PathVariable UUID idMembro
    ) {
        return this.membroService.consultarResponsaveisDoMembro(idMembro);
    }


}