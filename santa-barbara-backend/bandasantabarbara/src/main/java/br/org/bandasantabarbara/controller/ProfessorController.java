package br.org.bandasantabarbara.controller;

import br.org.bandasantabarbara.application.services.turma.ProfessorTurmaService;
import java.util.List;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.professores.ProfessorResponse;
import br.org.bandasantabarbara.application.services.ensino.ProfessorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/professores")
@Tag(name = "Professores", description = "Consulta de professores, alocações e instrumentos ensinados.")
public class ProfessorController {

    private final ProfessorTurmaService professorTurmaService;
    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService, ProfessorTurmaService professorTurmaService) {
        this.professorService = professorService;
        this.professorTurmaService = professorTurmaService;
    }


    @GetMapping
    @Operation(
            summary = "Listar professores",
            description = "Lista professores paginados, com busca por nome/e-mail e filtro por instrumentos ensinados."
    )
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO')")
    public PageResponse<ProfessorResponse> listar(
            @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC)
            Pageable pageable,
            @Parameter(description = "Trecho do nome ou e-mail do professor.")
            @RequestParam(required = false) String search,
            @Parameter(description = "Nomes dos instrumentos ensinados, separados por vírgula.")
            @RequestParam(required = false) List<String> instrumentosEnsinando
    ) {
        return professorService.listar(pageable, search, instrumentosEnsinando);
    }

    /*
    @GetMapping("/{idProfessor}/vinculos")
    @Operation(summary = "Listar turmas do professor", description = "Lista o histórico paginado de turmas do professor.")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO', 'PRESIDENTE', 'ALUNO')")
    public PageResponse<ProfessorTurmaResponseDTO> listarTurmas(
        @PathVariable UUID idProfessor,
        @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
        @RequestParam(required = false) ProfessorTurma.EnumStatusTurma status,
        @AuthenticationPrincipal Jwt jwt
    ) {
        UUID membroId = UUID.fromString(jwt.getSubject());
        List<String> papeis = jwt.getClaim("papeis");

        

        if(papeis
            .stream()
            .anyMatch(p -> 
                p.equals("SUPER_ADMIN") || 
                p.equals("ADMIN") ||
                p.equals("SECRETARIO") || 
                p.equals("PRESIDENTE"))
        ) {
           
            return professorTurmaService.listarDoProfessor(idProfessor, pageable, status);
        }

        if (membroId.equals(idProfessor)) {
            return professorTurmaService.listarDoProfessor(idProfessor, pageable, status);
        }

        throw new SecurityException("Acesso negado: você não tem permissão para acessar as turmas deste professor.");
    }

    @GetMapping("/{idProfessor}/turmas-atuais")
    @Operation(summary = "Listar turmas atuais do professor")
    @PreAuthorize("hasAuthority('PROFESSOR')")
    public PageResponse<ProfessorTurmaAtualResponse> listarTurmasAtuais(
        @PathVariable UUID idProfessor,
        @PageableDefault(page = 0, size = 20) Pageable pageable,
        @AuthenticationPrincipal Jwt jwt
    ) {
        validarAcessoDoProfessor(idProfessor, jwt);
        return professorService.listarTurmasAtuais(idProfessor, pageable);
    }

    @GetMapping("/{idProfessor}/turmas-atuais/horarios")
    @Operation(summary = "Listar horários atuais do professor")
    @PreAuthorize("hasAuthority('PROFESSOR')")
    public PageResponse<ProfessorTurmaHorarioResponse> listarHorariosAtuais(
        @PathVariable UUID idProfessor,
        @PageableDefault(page = 0, size = 20) Pageable pageable,
        @AuthenticationPrincipal Jwt jwt
    ) {
        validarAcessoDoProfessor(idProfessor, jwt);
        return professorService.listarHorariosAtuais(idProfessor, pageable);
    }

    private void validarAcessoDoProfessor(UUID idProfessor, Jwt jwt) {
        UUID membroId = UUID.fromString(jwt.getSubject());
        if (!membroId.equals(idProfessor)) {
            throw new ForbiddenException("Acesso negado: você só pode consultar suas próprias turmas.");
        }
    }
    */

}
