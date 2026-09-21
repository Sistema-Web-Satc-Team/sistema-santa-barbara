package br.org.bandasantabarbara.infrastructure.controller;

import br.org.bandasantabarbara.application.dtos.DefaultMessageResponse;
import br.org.bandasantabarbara.application.dtos.convite.ConvidarMembroRequest;
import br.org.bandasantabarbara.application.dtos.convite.ConvidarMembroResponse;
import br.org.bandasantabarbara.application.dtos.membros.RegistrarMembroRequest;
import br.org.bandasantabarbara.application.usecase.ConvidarMembro;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/convites")
public class ConviteController {

    private final ConvidarMembro convidarMembroUsecase;

    public ConviteController(ConvidarMembro convidarMembroUsecase) {
        this.convidarMembroUsecase = convidarMembroUsecase;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CONVITE:CONVIDAR')")
    public ConvidarMembroResponse convidarMembro(@RequestBody ConvidarMembroRequest dto) {
        return convidarMembroUsecase.executar(dto);
    }

}
