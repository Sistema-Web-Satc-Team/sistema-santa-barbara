package br.org.bandasantabarbara.controller;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.turmas.MatriculaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.MatriculaDetalheResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.MatriculaResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.DesistirMatriculaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.NovaMatriculaResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.TransferirMatriculaRequestDTO;
import br.org.bandasantabarbara.application.services.turma.MatriculaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/matriculas")
@Tag(name = "Matrículas", description = "Alocação e histórico de matrícula de alunos em turmas.")
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO', 'PRESIDENTE')")
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Matricular aluno", description = "Cria uma matrícula ativa após validar capacidade e conflitos de horário.")
    public NovaMatriculaResponseDTO matricular(@RequestBody @Valid MatriculaRequestDTO dto) {
        return toNovaMatricula(matriculaService.matricular(dto));
    }

    @GetMapping
    @Operation(summary = "Listar matrículas", description = "Lista matrículas paginadas, com filtro opcional por status.")
    public PageResponse<MatriculaResponseDTO> listar(
        @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
        @RequestParam(required = false) String status
    ) {
        return matriculaService.listar(pageable, status);
    }

    @GetMapping("/{idMatricula}")
    @Operation(summary = "Consultar matrícula", description = "Consulta os dados do aluno e o status de uma matrícula.")
    public MatriculaDetalheResponseDTO buscarPorId(@PathVariable Long idMatricula) {
        return matriculaService.buscarPorId(idMatricula);
    }

    @PostMapping("/{idMatricula}/desistir")
    @Operation(summary = "Registrar desistência", description = "Encerra a matrícula ativa e preserva seu histórico.")
    public MatriculaResponseDTO desistir(
        @PathVariable Long idMatricula,
        @RequestBody(required = false) @Valid DesistirMatriculaRequestDTO dto
    ) {
        return matriculaService.desistir(idMatricula, dto);
    }

    @PostMapping("/{idMatricula}/transferir")
    @Operation(summary = "Transferir matrícula", description = "Transfere um aluno entre turmas em andamento.")
    public NovaMatriculaResponseDTO transferir(
        @PathVariable Long idMatricula,
        @RequestBody @Valid TransferirMatriculaRequestDTO dto
    ) {
        return toNovaMatricula(matriculaService.transferir(idMatricula, dto));
    }

    @DeleteMapping("/{idMatricula}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remover matrícula", description = "Remove uma matrícula de uma turma em planejamento.")
    public void remover(@PathVariable Long idMatricula) {
        matriculaService.remover(idMatricula);
    }

    private NovaMatriculaResponseDTO toNovaMatricula(MatriculaResponseDTO matricula) {
        return new NovaMatriculaResponseDTO(
            matricula.id(),
            matricula.idAluno(),
            matricula.idTurma(),
            matricula.status(),
            matricula.justificativa()
        );
    }
}
