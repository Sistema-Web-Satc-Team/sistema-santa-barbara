package br.org.bandasantabarbara.infrastructure.controller;

import br.org.bandasantabarbara.application.dtos.convite.ConvidarMembroRequest;
import br.org.bandasantabarbara.application.dtos.convite.ConvidarMembroResponse;
import br.org.bandasantabarbara.application.dtos.convite.ReenviarConviteRequest;
import br.org.bandasantabarbara.application.usecase.convites.ConvidarMembro;
import br.org.bandasantabarbara.application.usecase.convites.ReenviarConvite;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/convites")
public class ConviteController {

    private final ConvidarMembro convidarMembroUsecase;
    private final ReenviarConvite reenviarConviteUsecase;

    public ConviteController(
            ConvidarMembro convidarMembroUsecase,
            ReenviarConvite reenviarConviteUsecase
    ) {
        this.convidarMembroUsecase = convidarMembroUsecase;
        this.reenviarConviteUsecase = reenviarConviteUsecase;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('CONVITE:CONVIDAR')")
    public ConvidarMembroResponse convidarMembro(@RequestBody ConvidarMembroRequest dto) {
        return convidarMembroUsecase.executar(dto);
    }


    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CONVITE:CONVIDAR')")
    public ConvidarMembroResponse reenviarConvite(@PathVariable("id") UUID idConvite) {
        var dto = new ReenviarConviteRequest(idConvite);
        return reenviarConviteUsecase.executar(dto);
    }

    /*@GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CONVITE:VISUALIZAR')")
    public void getConvite(@PathVariable("id") UUID idConvite) {

    }*/

}
