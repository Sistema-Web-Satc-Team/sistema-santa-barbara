package br.org.bandasantabarbara.controller;

import br.org.bandasantabarbara.application.dtos.turmas.MatriculaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.MatriculaResponseDTO;
import br.org.bandasantabarbara.application.services.turma.TurmaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/matriculas")
@Tag(name = "Matrículas", description = "Alocação e histórico de matrícula de alunos em turmas.")
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO', 'PRESIDENTE')")
public class MatriculaController {

    private final TurmaService turmaService;

    public MatriculaController(TurmaService turmaService) {
        this.turmaService = turmaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Matricular aluno", description = "Cria uma matrícula ativa após validar capacidade e conflitos de horário.")
    public MatriculaResponseDTO matricular(@RequestBody @Valid MatriculaRequestDTO dto) {
        return turmaService.matricular(dto);
    }

    @PostMapping("/{idMatricula}/desistir")
    @Operation(summary = "Registrar desistência", description = "Encerra a matrícula ativa e preserva seu histórico.")
    public MatriculaResponseDTO desistir(@PathVariable Long idMatricula) {
        return turmaService.desistirMatricula(idMatricula);
    }
}
