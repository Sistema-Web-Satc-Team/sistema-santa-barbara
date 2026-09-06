package br.org.bandasantabarbara.infrastructure.controller;

import br.org.bandasantabarbara.application.dtos.DefaultMessageResponse;
import br.org.bandasantabarbara.application.dtos.RegistrarAdminRequest;
import br.org.bandasantabarbara.application.usecase.SetupApplicationUsecase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/setup")
public class SetupApplicationController {

    private SetupApplicationUsecase usecase;

    public SetupApplicationController(SetupApplicationUsecase usecase) {
        this.usecase = usecase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DefaultMessageResponse registerAdmin(@RequestBody @Valid RegistrarAdminRequest dto) {
            usecase.registrarAdmin(dto);
            return new DefaultMessageResponse("Admin criado com sucesso.");
    }
}
