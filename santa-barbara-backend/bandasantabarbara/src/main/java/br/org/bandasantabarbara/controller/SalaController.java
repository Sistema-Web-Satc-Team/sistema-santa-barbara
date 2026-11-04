package br.org.bandasantabarbara.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.org.bandasantabarbara.application.dtos.sala.SalaRequestDTO;
import br.org.bandasantabarbara.application.dtos.sala.SalaResponseDTO;
import br.org.bandasantabarbara.application.services.turma.SalaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;



@RestController 
@RequestMapping("/salas")
@Tag(name = "Salas", description = "Cadastro e consulta de salas físicas.")
public class SalaController {
    
    private final SalaService salaService;

    public SalaController(SalaService salaService) {
        this.salaService = salaService;
    }

    @PostMapping
    @Operation(summary = "Cadastrar sala", description = "Cadastra uma sala com código e capacidade.")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO', 'PRESIDENTE')")
    public SalaResponseDTO cadastrar(
        @Valid @RequestBody SalaRequestDTO dto
    ) {
        return this.salaService.cadastrar(dto);
    }

    @GetMapping
    @Operation(summary = "Listar salas", description = "Lista salas, opcionalmente filtradas por capacidade.")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO', 'PRESIDENTE')")
    public List<SalaResponseDTO> listar(@RequestParam(required = false) Integer capacidade) {
        return this.salaService.listar(capacidade);
    }


    @GetMapping("/{code}")
    @Operation(summary = "Consultar sala", description = "Consulta uma sala pelo código.")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO', 'PRESIDENTE')")
    public SalaResponseDTO pegarPorCodigo(@PathVariable  String code) {
        return this.salaService.pegarPorCodigo(code);
    }

    @PutMapping("/{code}")
    @Operation(summary = "Atualizar sala", description = "Atualiza o código e a capacidade da sala.")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO', 'PRESIDENTE')")
    public SalaResponseDTO atualizar(
        @PathVariable String code,
        @Valid @RequestBody SalaRequestDTO dto
    ) {
        return salaService.atualizar(code, dto);
    }

    @DeleteMapping("/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Excluir sala", description = "Exclui uma sala somente quando nenhuma turma estiver associada.")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO', 'PRESIDENTE')")
    public void deletar(@PathVariable String code) {
        salaService.deletar(code);
    }
    
}
