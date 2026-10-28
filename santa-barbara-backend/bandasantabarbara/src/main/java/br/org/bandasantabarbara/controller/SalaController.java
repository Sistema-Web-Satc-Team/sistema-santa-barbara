package br.org.bandasantabarbara.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.org.bandasantabarbara.application.dtos.sala.SalaRequestDTO;
import br.org.bandasantabarbara.application.dtos.sala.SalaResponseDTO;
import br.org.bandasantabarbara.application.services.sala.SalaService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;



@RestController 
@RequestMapping(name = "/salas")
public class SalaController {
    
    private final SalaService salaService;

    public SalaController(SalaService salaService) {
        this.salaService = salaService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECREATARIO')")
    public SalaResponseDTO cadastrar(
        @Valid @RequestBody SalaRequestDTO dto
    ) {
        return this.salaService.cadastrar(dto);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECREATARIO')")
    public List<SalaResponseDTO> listar(@RequestParam(required = false) Integer capacidade) {
        return this.salaService.listar(capacidade);
    }


    @GetMapping("/{code}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECREATARIO')")
    public SalaResponseDTO pegarPorCodigo(@PathVariable  String code) {
        return this.salaService.pegarPorCodigo(code);
    }
    
}
