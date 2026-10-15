package br.org.bandasantabarbara.controller;


import br.org.bandasantabarbara.application.dtos.membros.FuncaoResponse;
import br.org.bandasantabarbara.application.services.funcoes.FuncaoService;
import br.org.bandasantabarbara.model.Funcao;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("funcoes")
public class FuncaoControlller {


    private final FuncaoService funcaoService;

    public FuncaoControlller (FuncaoService funcaoService) {
        this.funcaoService = funcaoService;
    }


    @GetMapping
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public List<FuncaoResponse> listarFuncoes() {
        return this.funcaoService.listarFuncoes();
    }
}
