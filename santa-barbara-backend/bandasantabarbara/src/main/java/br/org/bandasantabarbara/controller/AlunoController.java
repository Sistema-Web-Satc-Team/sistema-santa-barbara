package br.org.bandasantabarbara.controller;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.alunos.AlunoResponse;
import br.org.bandasantabarbara.application.dtos.turmas.MatriculaResponseDTO;
import br.org.bandasantabarbara.application.services.ensino.AlunoService;
import br.org.bandasantabarbara.application.services.turma.MatriculaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alunos")
@Tag(name = "Alunos", description = "Consulta dos alunos cadastrados.")
public class AlunoController {

    private final AlunoService alunoService;
    private final MatriculaService matriculaService;

    public AlunoController(AlunoService alunoService, MatriculaService matriculaService) {
        this.alunoService = alunoService;
        this.matriculaService = matriculaService;
    }

    @GetMapping
    @Operation(
            summary = "Listar alunos",
            description = "Lista alunos paginados, com busca por nome/e-mail, filtro por instrumentos aprendidos e opção para listar apenas menores."
    )
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO')")
    public PageResponse<AlunoResponse> listarAlunos(
            @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC)
            Pageable pageable,
            @Parameter(description = "Trecho do nome ou e-mail do aluno.")
            @RequestParam(required = false) String search,
            @Parameter(description = "Instrumentos aprendidos, separados por vírgula.")
            @RequestParam(required = false) List<String> instrumentosAprendendo,
            @Parameter(description = "Quando true, retorna apenas alunos menores de 18 anos.")
            @RequestParam(defaultValue = "false") boolean apenasMenores
    ) {
        return alunoService.listar(pageable, search, instrumentosAprendendo, apenasMenores);
    }

    @GetMapping("/{idAluno}/matriculas")
    @Operation(summary = "Listar matrículas do aluno", description = "Lista o histórico paginado de matrículas do aluno.")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO', 'PRESIDENTE', 'ALUNO')")
    public PageResponse<MatriculaResponseDTO> listarMatriculas(
        @PathVariable UUID idAluno,
        @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
        @RequestParam(required = false) String status,
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
           
            return matriculaService.listarDoAluno(idAluno, pageable, status);
        }

        if (membroId.equals(idAluno)) {
            return matriculaService.listarDoAluno(idAluno, pageable, status);
        }

        throw new SecurityException("Acesso negado: você não tem permissão para acessar as matrículas deste aluno.");
    }

}
