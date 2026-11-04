package br.org.bandasantabarbara.controller;

import br.org.bandasantabarbara.application.dtos.aulas.AulaChamadasResponse;
import br.org.bandasantabarbara.application.dtos.aulas.AulaListaResponse;
import br.org.bandasantabarbara.application.dtos.aulas.AulaResponse;
import br.org.bandasantabarbara.application.dtos.aulas.AtualizarChamadasRequest;
import br.org.bandasantabarbara.application.dtos.aulas.ChamadaResponse;
import br.org.bandasantabarbara.application.services.aula.AulaService;
import br.org.bandasantabarbara.model.aula.Aula;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/turmas/{idTurma}/aulas")
@Tag(name = "Aulas", description = "Início, encerramento, frequência e consultas de aulas.")
public class AulaController {

    private final AulaService aulaService;

    public AulaController(AulaService aulaService) {
        this.aulaService = aulaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('PROFESSOR')")
    @Operation(summary = "Iniciar aula", description = "Inicia uma aula durante um horário vigente da turma.")
    public AulaResponse iniciar(
        @PathVariable Long idTurma,
        @AuthenticationPrincipal Jwt jwt
    ) {
        return aulaService.iniciar(idTurma, UUID.fromString(jwt.getSubject()));
    }

    @PostMapping("/{idAula}/finalizar")
    @PreAuthorize("hasAuthority('PROFESSOR')")
    @Operation(summary = "Finalizar aula manualmente")
    public AulaResponse finalizar(
        @PathVariable Long idTurma,
        @PathVariable Long idAula,
        @AuthenticationPrincipal Jwt jwt
    ) {
        return aulaService.finalizar(idTurma, idAula, UUID.fromString(jwt.getSubject()));
    }

    @PostMapping("/{idAula}/reabrir")
    @PreAuthorize("hasAuthority('PROFESSOR')")
    @Operation(summary = "Reabrir aula")
    public AulaResponse reabrir(
        @PathVariable Long idTurma,
        @PathVariable Long idAula,
        @AuthenticationPrincipal Jwt jwt
    ) {
        return aulaService.reabrir(idTurma, idAula, UUID.fromString(jwt.getSubject()));
    }

    @PatchMapping("/{idAula}/chamadas")
    @PreAuthorize("hasAuthority('PROFESSOR')")
    @Operation(summary = "Registrar ou atualizar chamadas")
    public List<ChamadaResponse> atualizarChamadas(
        @PathVariable Long idTurma,
        @PathVariable Long idAula,
        @AuthenticationPrincipal Jwt jwt,
        @RequestBody @Valid AtualizarChamadasRequest request
    ) {
        return aulaService.atualizarChamadas(idTurma, idAula, UUID.fromString(jwt.getSubject()), request);
    }

    @GetMapping("/{idAula}/chamadas")
    @PreAuthorize("hasAnyAuthority('PROFESSOR', 'SECRETARIO', 'ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Consultar chamadas da aula")
    public AulaChamadasResponse buscarChamadas(
        @PathVariable Long idTurma,
        @PathVariable Long idAula,
        @AuthenticationPrincipal Jwt jwt,
        Authentication authentication
    ) {
        return aulaService.buscarChamadas(
            idTurma,
            idAula,
            UUID.fromString(jwt.getSubject()),
            podeConsultarTodas(authentication)
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('PROFESSOR', 'SECRETARIO', 'ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Listar aulas", description = "Lista aulas de forma paginada, com filtro opcional por encerramento.")
    public AulaListaResponse listar(
        @PathVariable Long idTurma,
        @AuthenticationPrincipal Jwt jwt,
        Authentication authentication,
        @PageableDefault(page = 0, size = 20, sort = "dataHoraInicio", direction = Sort.Direction.DESC)
        Pageable pageable,
        @RequestParam(required = false) Aula.EnumTipoEncerramento tipoEncerramento
    ) {
        return aulaService.listar(
            idTurma,
            UUID.fromString(jwt.getSubject()),
            podeConsultarTodas(authentication),
            tipoEncerramento,
            pageable
        );
    }

    private boolean podeConsultarTodas(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .map(authority -> authority.getAuthority())
            .anyMatch(List.of("SECRETARIO", "ADMIN", "SUPER_ADMIN")::contains);
    }
}
