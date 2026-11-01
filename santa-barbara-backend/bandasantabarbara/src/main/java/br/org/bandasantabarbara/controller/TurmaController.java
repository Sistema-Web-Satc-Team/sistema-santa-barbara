package br.org.bandasantabarbara.controller;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.turmas.AtualizarTurmaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.CriarTurmaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.ProfessorTurmaResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.TurmaResponseDTO;
import br.org.bandasantabarbara.application.services.turma.TurmaService;
import br.org.bandasantabarbara.model.turma.StatusTurma;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @PutMapping("/{idTurma}")
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

    @GetMapping
    @Operation(summary = "Listar turmas", description = "Lista turmas paginadas, com filtro opcional por status.")
    public PageResponse<TurmaResponseDTO> listar(
        @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.ASC)
        Pageable pageable,
        @Parameter(description = "Status atual da turma.")
        @RequestParam(required = false) StatusTurma status
    ) {
        return turmaService.listar(pageable, status);
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

    @PostMapping("/{idTurma}/encerrar")
    @Operation(summary = "Encerrar turma", description = "Encerra a turma e todos os vínculos ativos em uma transação.")
    public TurmaResponseDTO encerrar(@PathVariable Long idTurma) {
        return turmaService.encerrar(idTurma);
    }
}
