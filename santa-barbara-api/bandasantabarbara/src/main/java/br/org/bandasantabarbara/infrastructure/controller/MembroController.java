package br.org.bandasantabarbara.infrastructure.controller;

import br.org.bandasantabarbara.application.dtos.DefaultMessageResponse;
import br.org.bandasantabarbara.application.dtos.RegistrarMembroRequest;
import br.org.bandasantabarbara.application.usecase.MembroUsecase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/membros")
public class MembroController {

    private final MembroUsecase usecase;

    public MembroController(MembroUsecase usecase) {
        this.usecase = usecase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DefaultMessageResponse registrarMembro(@RequestBody @Valid RegistrarMembroRequest dto) {
        usecase.registrarMembro(dto);
        return new DefaultMessageResponse("Membro adicionado com sucesso.");
    }
}
