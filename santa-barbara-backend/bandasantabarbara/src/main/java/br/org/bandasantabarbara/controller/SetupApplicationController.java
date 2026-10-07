package br.org.bandasantabarbara.controller;

import br.org.bandasantabarbara.application.dtos.MessageResponse;
import br.org.bandasantabarbara.application.dtos.setup.RegistrarAdminRequest;
import br.org.bandasantabarbara.application.usecase.init.SetupApplicationUsecase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/setup")
public class SetupApplicationController {

    private final SetupApplicationUsecase setupApplicationUsecase;

    public SetupApplicationController(SetupApplicationUsecase setupApplicationUsecase) {
        this.setupApplicationUsecase = setupApplicationUsecase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse registerAdmin(@RequestBody @Valid RegistrarAdminRequest dto) {
        setupApplicationUsecase.registrarAdmin(dto);
        return new MessageResponse("Admin criado com sucesso.");
    }
}
