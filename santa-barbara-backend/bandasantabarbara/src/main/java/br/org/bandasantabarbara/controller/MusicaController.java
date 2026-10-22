package br.org.bandasantabarbara.controller;


import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.musicas.MusicaRequest;
import br.org.bandasantabarbara.application.dtos.musicas.MusicaResponse;
import br.org.bandasantabarbara.application.dtos.partituras.PartituraRequest;
import br.org.bandasantabarbara.application.dtos.partituras.PartituraResponse;
import br.org.bandasantabarbara.application.usecase.musica.*;
import br.org.bandasantabarbara.application.usecase.partitura.AdicionarPartituraMusicaUseCase;
import br.org.bandasantabarbara.application.usecase.partitura.AtualizarPartituraUseCase;
import br.org.bandasantabarbara.application.usecase.partitura.ListarPartiturasMusicaUseCase;
import br.org.bandasantabarbara.application.usecase.partitura.RemoverPartituraUseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.bind.annotation.RequestBody;
import tools.jackson.databind.ObjectMapper;

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

    private final AdicionarPartituraMusicaUseCase adicionarPartituraMusicaUseCase;
    private final ListarPartiturasMusicaUseCase listarPartiturasMusicaUseCase;
    private final RemoverPartituraUseCase removerPartituraUseCase;
    private final AtualizarPartituraUseCase atualizarPartituraUseCase;


    public MusicaController(
            CadastrarMusicaUseCase cadastrarMusicaUseCase,
            SugerirGeneroUseCase sugerirGeneroUseCase,
            ConsultarMusicaUseCase consultarMusicaUseCase,
            AtualizarMusicaUseCase atualizarMusicaUseCase,
            RemoverMusicaUseCase removerMusicaUseCase,
            AdicionarPartituraMusicaUseCase adicionarPartituraMusicaUseCase,
            ListarPartiturasMusicaUseCase listarPartiturasMusicaUseCase,
            RemoverPartituraUseCase removerPartituraUseCase,
            AtualizarPartituraUseCase atualizarPartituraUseCase) {
        this.cadastrarMusicaUseCase = cadastrarMusicaUseCase;
        this.sugerirGeneroUseCase = sugerirGeneroUseCase;
        this.consultarMusicaUseCase = consultarMusicaUseCase;
        this.removerMusicaUseCase = removerMusicaUseCase;
        this.atualizarMusicaUseCase = atualizarMusicaUseCase;
        this.adicionarPartituraMusicaUseCase = adicionarPartituraMusicaUseCase;
        this.listarPartiturasMusicaUseCase = listarPartiturasMusicaUseCase;
        this.removerPartituraUseCase = removerPartituraUseCase;
        this.atualizarPartituraUseCase = atualizarPartituraUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'MAESTRO')")
    public MusicaResponse cadastrar(
            @Valid @RequestBody MusicaRequest dto
    ) {
        return this.cadastrarMusicaUseCase.cadastrar(dto);
    }


    @GetMapping("/generos/sugestoes")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'ALUNO', 'MAESTRO', 'MUSICO_BANDA', 'PROFESSOR', 'RESPONSAVEL')")
    public List<String> sugestionarGeneros(@RequestParam String q) {
        return this.sugerirGeneroUseCase.sugerir(q);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'ALUNO', 'MAESTRO', 'MUSICO_BANDA', 'PROFESSOR', 'RESPONSAVEL')")
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
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'MAESTRO')")
    public ResponseEntity<MusicaResponse> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody MusicaRequest request) {
        return ResponseEntity.ok(this.atualizarMusicaUseCase.executar(id, request));
    }

    /*
    *
    * Partituras da música
    *
     */

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'MAESTRO')")
    public ResponseEntity<Void> remover(@PathVariable UUID id) {
        this.removerMusicaUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{musicaId}/partituras", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'MAESTRO')")
    public PartituraResponse cadastrarPartituras(
            @PathVariable UUID musicaId,
            @RequestPart("file") MultipartFile file,
            @RequestParam("request") String requestJson,
            ObjectMapper objectMapper
    ) {
        PartituraRequest partituraRequest = objectMapper.readValue(requestJson, PartituraRequest.class);
        return this.adicionarPartituraMusicaUseCase.adicionar(musicaId, file, partituraRequest);
    }

    @GetMapping("/{musicaId}/partituras")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'RESPONSAVEL', 'ADMIN', 'MAESTRO', 'ALUNO', 'MUSICO_BANDA', 'PROFESSOR')")
    public List<PartituraResponse> listarPartiturasDaMusica(
            @PathVariable UUID musicaId
    ) {
        return this.listarPartiturasMusicaUseCase.executar(musicaId);
    }

    @DeleteMapping("/partituras/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'MAESTRO')")
    public ResponseEntity<Void> removerPartitura(@PathVariable UUID id) {
        this.removerPartituraUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/partituras/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'MAESTRO')")
    public PartituraResponse atualizarPartitura(
            @PathVariable UUID id,
            @RequestBody @Valid PartituraRequest request
    ) {
        return this.atualizarPartituraUseCase.executar(id, request);
    }



}
