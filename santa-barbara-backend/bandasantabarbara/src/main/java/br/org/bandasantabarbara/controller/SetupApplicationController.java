package br.org.bandasantabarbara.controller;

import br.org.bandasantabarbara.application.dtos.MessageResponse;
import br.org.bandasantabarbara.application.dtos.setup.RegistrarAdminRequest;
import br.org.bandasantabarbara.application.services.setup.SetupApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/setup")
@Tag(name = "Configuração inicial", description = "Operações iniciais de configuração do sistema.")
public class SetupApplicationController {

    private final SetupApplicationService setupApplicationService;

    public SetupApplicationController(SetupApplicationService setupApplicationService) {
        this.setupApplicationService = setupApplicationService;
    }

    @PostMapping
    @Operation(summary = "Registrar administrador inicial", description = "Cria o primeiro administrador do sistema.")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse registerAdmin(@RequestBody @Valid RegistrarAdminRequest dto) {
        setupApplicationService.registrarAdmin(dto);
        return new MessageResponse("Admin criado com sucesso.");
    }
}
