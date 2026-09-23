package br.org.bandasantabarbara.infrastructure.controller;

import br.org.bandasantabarbara.application.dtos.DefaultMessageResponse;
import br.org.bandasantabarbara.application.dtos.OffsetPaginationRequest;
import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.convite.*;
import br.org.bandasantabarbara.application.dtos.membros.ListMembroRequest;
import br.org.bandasantabarbara.application.dtos.membros.MembroResponse;
import br.org.bandasantabarbara.application.filters.MembroFilter;
import br.org.bandasantabarbara.application.usecase.convites.AceitarConvite;
import br.org.bandasantabarbara.application.usecase.convites.ConsultarConvite;
import br.org.bandasantabarbara.application.usecase.convites.ConvidarMembro;
import br.org.bandasantabarbara.application.usecase.convites.ReenviarConvite;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/convites")
public class ConviteController {

    private final ConvidarMembro convidarMembroUsecase;
    private final ReenviarConvite reenviarConviteUsecase;
    private final ConsultarConvite consultarConviteUsecase;
    private final AceitarConvite aceitarConviteUsecase;

    public ConviteController(
            ConvidarMembro convidarMembroUsecase,
            ReenviarConvite reenviarConviteUsecase,
            ConsultarConvite consultarConviteUsecase,
            AceitarConvite aceitarConviteUsecase
    ) {
        this.convidarMembroUsecase = convidarMembroUsecase;
        this.reenviarConviteUsecase = reenviarConviteUsecase;
        this.consultarConviteUsecase = consultarConviteUsecase;
        this.aceitarConviteUsecase = aceitarConviteUsecase;
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

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CONVITE:VISUALIZAR')")
    @ResponseStatus(HttpStatus.OK)
    public ConsultarConviteResponse getConvite(@PathVariable("id") UUID idConvite) {
        return this.consultarConviteUsecase.pegarPorId(idConvite);
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('CONVITE:LISTAR')")
    public PageResponse<ConsultarConviteResponse> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var offsetPaginationRequest = new OffsetPaginationRequest(page, size);

        return this.consultarConviteUsecase.listar(offsetPaginationRequest);
    }


    @GetMapping("/validar/{id}")
    @ResponseStatus(HttpStatus.OK)
    public boolean isValido(@PathVariable("id") UUID idConvite) {
        return this.consultarConviteUsecase.isConviteValido(idConvite);
    }


    @PostMapping("/aceitar")
    @ResponseStatus(HttpStatus.OK)
    public DefaultMessageResponse aceitarConvite(@RequestBody AceitarConviteRequest request) {
        this.aceitarConviteUsecase.executar(request);

        return new DefaultMessageResponse("Convite aceito com sucesso!");
    }
}
