package br.org.bandasantabarbara.controller;


import br.org.bandasantabarbara.application.dtos.musicas.MusicaRequest;
import br.org.bandasantabarbara.application.dtos.musicas.MusicaResponse;
import br.org.bandasantabarbara.application.usecase.musica.CadastrarMusicaUseCase;
import br.org.bandasantabarbara.application.usecase.musica.SugerirGeneroUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/musicas")
public class MusicaController {

    private final CadastrarMusicaUseCase cadastrarMusicaUseCase;
    private final SugerirGeneroUseCase sugerirGeneroUseCase;

    public MusicaController(CadastrarMusicaUseCase cadastrarMusicaUseCase, SugerirGeneroUseCase sugerirGeneroUseCase) {
        this.cadastrarMusicaUseCase = cadastrarMusicaUseCase;
        this.sugerirGeneroUseCase = sugerirGeneroUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public MusicaResponse cadastrar(
            @Valid @RequestBody MusicaRequest dto
    ) {
        return this.cadastrarMusicaUseCase.cadastrar(dto);
    }


    @GetMapping("/generos/sugestoes")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public List<String> sugestionarGeneros(@RequestParam String q) {
        return this.sugerirGeneroUseCase.sugerir(q);
    }
}
