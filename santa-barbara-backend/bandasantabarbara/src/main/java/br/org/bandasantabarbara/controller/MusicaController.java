package br.org.bandasantabarbara.controller;


import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.musicas.MusicaRequest;
import br.org.bandasantabarbara.application.dtos.musicas.MusicaResponse;
import br.org.bandasantabarbara.application.usecase.musica.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/musicas")
public class MusicaController {

    private final CadastrarMusicaUseCase cadastrarMusicaUseCase;
    private final SugerirGeneroUseCase sugerirGeneroUseCase;
    private final ConsultarMusicaUseCase consultarMusicaUseCase;
    private final AtualizarMusicaUseCase atualizarMusicaUseCase;
    private final RemoverMusicaUseCase removerMusicaUseCase;

    public MusicaController(
            CadastrarMusicaUseCase cadastrarMusicaUseCase,
            SugerirGeneroUseCase sugerirGeneroUseCase,
            ConsultarMusicaUseCase consultarMusicaUseCase,
            AtualizarMusicaUseCase atualizarMusicaUseCase,
            RemoverMusicaUseCase removerMusicaUseCase) {
        this.cadastrarMusicaUseCase = cadastrarMusicaUseCase;
        this.sugerirGeneroUseCase = sugerirGeneroUseCase;
        this.consultarMusicaUseCase = consultarMusicaUseCase;
        this.removerMusicaUseCase = removerMusicaUseCase;
        this.atualizarMusicaUseCase = atualizarMusicaUseCase;
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

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<MusicaResponse> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody MusicaRequest request) {
        return ResponseEntity.ok(this.atualizarMusicaUseCase.executar(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<Void> remover(@PathVariable UUID id) {
        this.removerMusicaUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }
}
