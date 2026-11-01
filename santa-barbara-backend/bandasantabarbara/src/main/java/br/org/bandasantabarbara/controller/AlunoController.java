package br.org.bandasantabarbara.controller;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.alunos.AlunoResponse;
import br.org.bandasantabarbara.application.services.ensino.AlunoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/alunos")
@Tag(name = "Alunos", description = "Consulta dos alunos cadastrados.")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
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

}
