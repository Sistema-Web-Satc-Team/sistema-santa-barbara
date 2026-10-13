package br.org.bandasantabarbara.controller;


import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.musicas.MusicaRequest;
import br.org.bandasantabarbara.application.dtos.musicas.MusicaResponse;
import br.org.bandasantabarbara.application.usecase.musica.CadastrarMusicaUseCase;
import br.org.bandasantabarbara.application.usecase.musica.ConsultarMusicaUseCase;
import br.org.bandasantabarbara.application.usecase.musica.SugerirGeneroUseCase;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/musicas")
public class MusicaController {

    private final CadastrarMusicaUseCase cadastrarMusicaUseCase;
    private final SugerirGeneroUseCase sugerirGeneroUseCase;
    private final ConsultarMusicaUseCase consultarMusicaUseCase;

    public MusicaController(CadastrarMusicaUseCase cadastrarMusicaUseCase, SugerirGeneroUseCase sugerirGeneroUseCase, ConsultarMusicaUseCase consultarMusicaUseCase) {
        this.cadastrarMusicaUseCase = cadastrarMusicaUseCase;
        this.sugerirGeneroUseCase = sugerirGeneroUseCase;
        this.consultarMusicaUseCase = consultarMusicaUseCase;
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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public PageResponse<MusicaResponse> listar(
            @PageableDefault(
                    size = 10,
                    sort = "titulo",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable,
            @RequestParam(required = false) String search
    ) {
        return this.consultarMusicaUseCase.listar(search, pageable);
    }
}
