package br.org.bandasantabarbara.controller;

import br.org.bandasantabarbara.application.dtos.MessageResponse;
import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.convite.*;
import br.org.bandasantabarbara.application.usecase.convites.AceitarConvite;
import br.org.bandasantabarbara.application.usecase.convites.ConsultarConvite;
import br.org.bandasantabarbara.application.usecase.convites.ConvidarMembro;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/convites")
public class ConviteController {

    private final ConvidarMembro convidarMembroUsecase;
    private final ConsultarConvite consultarConviteUsecase;
    private final AceitarConvite aceitarConviteUsecase;

    public ConviteController(
            ConvidarMembro convidarMembroUsecase,
            ConsultarConvite consultarConviteUsecase,
            AceitarConvite aceitarConviteUsecase
    ) {
        this.convidarMembroUsecase = convidarMembroUsecase;
        this.consultarConviteUsecase = consultarConviteUsecase;
        this.aceitarConviteUsecase = aceitarConviteUsecase;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ConvidarMembroResponse convidarMembro(@RequestBody ConvidarMembroRequest dto) {
        return convidarMembroUsecase.convidar(dto);
    }


    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ConvidarMembroResponse reenviarConvite(@PathVariable("id") UUID idConvite, @RequestBody CanalDTO request) {
        var dto = new ReenviarConviteRequest(idConvite, request.canal());
        return convidarMembroUsecase.reenviarConvite(dto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ConsultarConviteResponse getConvite(@PathVariable("id") UUID idConvite) {
        return this.consultarConviteUsecase.pegarPorId(idConvite);
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public PageResponse<ConsultarConviteResponse> listar(
            @PageableDefault(
                    size = 20,
                    sort = "nome",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable
    ) {

        return this.consultarConviteUsecase.listar(pageable);
    }


    @GetMapping("/validar/{id}")
    @ResponseStatus(HttpStatus.OK)
    public boolean isValido(@PathVariable("id") UUID idConvite) {
        return this.consultarConviteUsecase.isConviteValido(idConvite);
    }


    @PostMapping("/aceitar")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse aceitarConvite(@RequestBody AceitarConviteRequest request) {
        this.aceitarConviteUsecase.executar(request);

        return new MessageResponse("Convite aceito com sucesso!");
    }
}
