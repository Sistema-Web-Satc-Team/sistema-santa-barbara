package br.org.bandasantabarbara.controller;

import br.org.bandasantabarbara.application.dtos.MessageResponse;
import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.convite.*;
import br.org.bandasantabarbara.application.services.membro.ConviteService;
import br.org.bandasantabarbara.model.membro.EnumConviteStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/convites")
@Tag(name = "Convites", description = "Criação, consulta e aceite de convites de acesso.")
public class ConviteController {

    private final ConviteService conviteService;

    public ConviteController(ConviteService conviteService) {
        this.conviteService = conviteService;
    }


    @PostMapping
    @Operation(summary = "Convidar membro", description = "Cria e envia um convite de acesso para um membro.")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO')")
    public ConvidarMembroResponse convidarMembro(@RequestBody ConvidarMembroRequest dto) {
        return conviteService.convidar(dto);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Reenviar convite", description = "Reenvia um convite existente pelo canal informado.")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO')")
    public ConvidarMembroResponse reenviarConvite(@PathVariable("id") UUID idConvite, @RequestBody CanalDTO request) {
        var dto = new ReenviarConviteRequest(idConvite, request.canal());
        return conviteService.reenviarConvite(dto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar convite", description = "Retorna os dados de um convite pelo identificador.")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO')")
    public ConsultarConviteResponse getConvite(@PathVariable("id") UUID idConvite) {
        return this.conviteService.pegarPorId(idConvite);
    }


    @GetMapping
    @Operation(summary = "Listar convites", description = "Lista convites paginados, com filtros opcionais por status e busca.")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'SECRETARIO')")
    public PageResponse<ConsultarConviteResponse> listar(
            @PageableDefault(
                    size = 20,
                    sort = "dataCriacaoConvite",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable,
            @RequestParam(required = false) EnumConviteStatus status,
            @RequestParam(required = false) String search
    ) {

        return this.conviteService.listar(search, status, pageable);
    }


    @GetMapping("/validar/{id}")
    @Operation(summary = "Validar convite", description = "Verifica se o convite ainda pode ser aceito.")
    @ResponseStatus(HttpStatus.OK)
    public boolean isValido(@PathVariable("id") UUID idConvite) {
        return this.conviteService.isConviteValido(idConvite);
    }


    @PostMapping("/aceitar")
    @Operation(summary = "Aceitar convite", description = "Define as credenciais de acesso e aceita o convite.")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse aceitarConvite(@RequestBody AceitarConviteRequest request) {
        this.conviteService.aceitarConvite(request);

        return new MessageResponse("Convite aceito com sucesso!");
    }
}
