package br.org.bandasantabarbara.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.turmas.CriarTurmaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.TurmaResponseDTO;
import br.org.bandasantabarbara.application.services.turma.TurmaService;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController 
@RequestMapping("/turmas")
@Tag(name = "Turmas", description = "Cadastro e consulta das turmas de ensino.")
public class TurmaController {
    
    private final TurmaService turmaService;

    public TurmaController(TurmaService turmaService) {
        this.turmaService = turmaService;
    }

    @PostMapping
    @Operation(summary = "Cadastrar turma", description = "Cria uma turma e associa sala, período e horários.")
    public TurmaResponseDTO registrar(
        @RequestBody @Valid CriarTurmaRequestDTO dto
    ) {
        return this.turmaService.criar(dto);
    }


    @GetMapping
    @Operation(summary = "Listar turmas", description = "Lista turmas paginadas.")
    public PageResponse<TurmaResponseDTO> listar(
        @PageableDefault(
            page = 0, 
            size = 20, 
            sort = "id", 
            direction = Sort.Direction.ASC
        ) Pageable pageable
    ) {
        return this.turmaService.listar(pageable);
    }



}
