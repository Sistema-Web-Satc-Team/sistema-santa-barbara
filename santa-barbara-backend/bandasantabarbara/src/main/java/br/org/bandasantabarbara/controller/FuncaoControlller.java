package br.org.bandasantabarbara.controller;


import br.org.bandasantabarbara.application.dtos.membros.FuncaoResponse;
import br.org.bandasantabarbara.application.services.membro.FuncaoService;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.membro.Funcao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("funcoes")
@Tag(name = "Funções", description = "Consulta das funções disponíveis para vínculos.")
public class FuncaoControlller {


    private final FuncaoService funcaoService;

    public FuncaoControlller (FuncaoService funcaoService) {
        this.funcaoService = funcaoService;
    }


    @GetMapping
    @Operation(summary = "Listar funções", description = "Lista as funções que podem ser atribuídas conforme o papel do solicitante.")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO')")
    public List<FuncaoResponse> listarFuncoes(@AuthenticationPrincipal Jwt jwt) {
        List<String> papeis = jwt.getClaim("papeis");

        List<String> hierarquia = List.of("SUPER_ADMIN", "ADMIN", "SECRETARIO");

        String papelMaisForte = hierarquia.stream()
                .filter(papeis::contains)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Nenhum papel válido encontrado"));

        return this.funcaoService.listarFuncoes(papelMaisForte);
    }
}
