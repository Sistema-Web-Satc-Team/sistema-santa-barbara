package br.org.bandasantabarbara.controller;

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
@RequestMapping({"/professores", "/profesores"})
@Tag(name = "Professores", description = "Consulta de professores, alocações e instrumentos ensinados.")
public class ProfessorController {

    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
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
}
