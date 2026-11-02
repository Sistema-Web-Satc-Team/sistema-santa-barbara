package br.org.bandasantabarbara.controller;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.turmas.AtualizarTurmaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.CriarTurmaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.GradeTurmaResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.MatriculaDetalheResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.ProfessorTurmaResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.SubstituirProfessorRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.TurmaResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.TurmaResponseResumidoDTO;
import br.org.bandasantabarbara.application.services.turma.TurmaService;
import br.org.bandasantabarbara.model.turma.MatriculaAlunoTurma;
import br.org.bandasantabarbara.model.turma.ProfessorTurma;
import br.org.bandasantabarbara.model.turma.StatusTurma;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import java.util.List;
import java.util.Map;
import br.org.bandasantabarbara.application.dtos.turmas.TurmaAlunoResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.TurmaProfessorResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/turmas")
@Tag(name = "Turmas", description = "Cadastro e gerenciamento do ciclo de vida das turmas.")
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO', 'PRESIDENTE')")
public class TurmaController {

    private final TurmaService turmaService;

    public TurmaController(TurmaService turmaService) {
        this.turmaService = turmaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar turma", description = "Cria uma turma e associa sala, período, turno, horários e instrumentos.")
    public TurmaResponseDTO registrar(@RequestBody @Valid CriarTurmaRequestDTO dto) {
        return turmaService.criar(dto);
    }

    @PatchMapping("/{idTurma}")
    @Operation(summary = "Atualizar turma", description = "Atualiza dados, sala, período, turno, horários e instrumentos da turma.")
    public TurmaResponseDTO atualizar(
        @PathVariable Long idTurma,
        @RequestBody @Valid AtualizarTurmaRequestDTO dto
    ) {
        return turmaService.atualizar(idTurma, dto);
    }

    @GetMapping("/{idTurma}")
    @Operation(summary = "Consultar turma", description = "Consulta os dados, horários, sala e instrumentos de uma turma.")
    public TurmaResponseDTO buscarPorId(@PathVariable Long idTurma) {
        return turmaService.buscarPorId(idTurma);
    }

    @GetMapping("/{idTurma}/alunos")
    @Operation(summary = "Listar alunos ativos", description = "Lista somente as matrículas ativas da turma.")
    public List<MatriculaDetalheResponseDTO> listarAlunos(
        @PathVariable Long idTurma,
        @RequestParam(required = false) MatriculaAlunoTurma.EnumStatusMatricula status
    ) {
        return turmaService.buscarAlunosTurmaPorId(idTurma, status);
    }

    @GetMapping("/{idTurma}/professores")
    @Operation(summary = "Listar professores ativos", description = "Lista somente os vínculos ativos da turma.")
    public List<ProfessorTurmaResponseDTO> listarProfessores(
        @PathVariable Long idTurma,
        @RequestParam(required = false) ProfessorTurma.EnumStatusTurma status
    ) {
        return turmaService.buscarProfessoresTurmaPorId(idTurma, status);
    }

    @GetMapping
    @Operation(summary = "Listar turmas", description = "Lista turmas paginadas, com filtro opcional por status.")
    public PageResponse<TurmaResponseResumidoDTO> listar(
        @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.ASC)
        Pageable pageable,
        @Parameter(description = "Status atual da turma.")
        @RequestParam(required = false) StatusTurma status
    ) {
        return turmaService.listar(pageable, status);
    }

    @GetMapping("/grade-horarios")
    @Operation(summary = "Consultar grade de horários", description = "Lista turmas que ainda estão em planejamento.")
    public PageResponse<GradeTurmaResponseDTO> listarGradeHorarios(
        @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return turmaService.listarGradeHorarios(pageable);
    }

    @PostMapping("/{idTurma}/professores/{idProfessor}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Vincular professor", description = "Cria um vínculo ativo entre um professor e a turma.")
    public ProfessorTurmaResponseDTO vincularProfessor(
        @PathVariable Long idTurma,
        @PathVariable UUID idProfessor
    ) {
        return turmaService.vincularProfessor(idTurma, idProfessor);
    }

    @DeleteMapping("/{idTurma}/professores/{idProfessor}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remover professor", description = "Remove um vínculo ativo de professor de uma turma em planejamento.")
    public void removerProfessor(
        @PathVariable Long idTurma,
        @PathVariable UUID idProfessor
    ) {
        turmaService.removerProfessor(idTurma, idProfessor);
    }

    @PostMapping("/{idTurma}/professores/substituir")
    @Operation(summary = "Substituir professor", description = "Substitui o professor ativo de uma turma em andamento.")
    public ProfessorTurmaResponseDTO substituirProfessor(
        @PathVariable Long idTurma,
        @RequestBody @Valid SubstituirProfessorRequestDTO dto
    ) {
        return turmaService.substituirProfessor(idTurma, dto.idNovoProfessor(), dto.justificativa());
    }

    @PostMapping("/{idTurma}/cancelar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Cancelar turma", description = "Cancela uma turma que ainda está em planejamento.")
    public void cancelar(@PathVariable Long idTurma) {
        turmaService.cancelar(idTurma);
    }

    @PostMapping("/{idTurma}/interromper")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Interromper turma", description = "Interrompe uma turma em andamento e encerra seus vínculos.")
    public void interromper(@PathVariable Long idTurma) {
        turmaService.interromper(idTurma);
    }
}
