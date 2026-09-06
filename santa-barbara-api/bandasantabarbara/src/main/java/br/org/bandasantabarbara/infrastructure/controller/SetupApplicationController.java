package br.org.bandasantabarbara.infrastructure.controller;

import br.org.bandasantabarbara.application.DefaultMessageResponse;
import br.org.bandasantabarbara.application.RegisterAdminDTO;
import br.org.bandasantabarbara.application.SetupApplicationUsecase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.ServerResponse;

@RestController
@RequestMapping("/setup")
public class SetupApplicationController {

    private SetupApplicationUsecase usecase;

    public SetupApplicationController(SetupApplicationUsecase usecase) {
        this.usecase = usecase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DefaultMessageResponse registerAdmin(@RequestBody @Valid RegisterAdminDTO dto) {
            usecase.registrarAdmin(dto);
            return new DefaultMessageResponse("Admin criado com sucesso.");
    }
}
