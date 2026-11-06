package br.org.bandasantabarbara.controller;


import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.musicas.MusicaRequest;
import br.org.bandasantabarbara.application.dtos.musicas.MusicaResponse;
import br.org.bandasantabarbara.application.dtos.partituras.PartituraRequest;
import br.org.bandasantabarbara.application.dtos.partituras.PartituraResponse;
import br.org.bandasantabarbara.application.services.musica.GeneroService;
import br.org.bandasantabarbara.application.services.musica.MusicaService;
import br.org.bandasantabarbara.application.services.musica.PartituraService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Músicas e partituras", description = "Cadastro de músicas, gêneros e partituras.")
public class MusicaController {

    private final MusicaService musicaService;
    private final GeneroService generoService;
    private final PartituraService partituraService;


    public MusicaController(
            MusicaService musicaService,
            GeneroService generoService,
            PartituraService partituraService) {
        this.musicaService = musicaService;
        this.generoService = generoService;
        this.partituraService = partituraService;
    }

    @PostMapping
    @Operation(summary = "Cadastrar música", description = "Cadastra uma música e associa seus gêneros.")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'MAESTRO')")
    public MusicaResponse cadastrar(
            @Valid @RequestBody MusicaRequest dto
    ) {
        return this.musicaService.cadastrar(dto);
    }


    @GetMapping("/generos/sugestoes")
    @Operation(summary = "Sugerir gêneros", description = "Busca sugestões de gêneros a partir de um termo.")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'ALUNO', 'MAESTRO', 'MUSICO_BANDA', 'PROFESSOR', 'RESPONSAVEL')")
    public List<String> sugestionarGeneros(@RequestParam String q) {
        return this.generoService.sugerir(q);
    }

    @GetMapping
    @Operation(summary = "Listar músicas", description = "Lista músicas paginadas, com busca opcional.")
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
        return this.musicaService.listar(search, pageable);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar música", description = "Atualiza os dados de uma música.")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'MAESTRO')")
    public ResponseEntity<MusicaResponse> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody MusicaRequest request) {
        return ResponseEntity.ok(this.musicaService.atualizar(id, request));
    }

    /*
    *
    * Partituras da música
    *
     */

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover música", description = "Remove uma música.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'MAESTRO')")
    public ResponseEntity<Void> remover(@PathVariable UUID id) {
        this.musicaService.remover(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{musicaId}/partituras", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Adicionar partitura", description = "Envia o arquivo e os metadados de uma partitura para a música.")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'MAESTRO')")
    public PartituraResponse cadastrarPartituras(
            @PathVariable UUID musicaId,
            @RequestPart("file") MultipartFile file,
            @RequestParam("request") String requestJson,
            ObjectMapper objectMapper
    ) {
        PartituraRequest partituraRequest = objectMapper.readValue(requestJson, PartituraRequest.class);
        return this.partituraService.adicionar(musicaId, file, partituraRequest);
    }

    @GetMapping("/{musicaId}/partituras")
    @Operation(summary = "Listar partituras da música", description = "Lista as partituras associadas a uma música.")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'RESPONSAVEL', 'ADMIN', 'MAESTRO', 'ALUNO', 'MUSICO_BANDA', 'PROFESSOR')")
    public List<PartituraResponse> listarPartiturasDaMusica(
            @PathVariable UUID musicaId
    ) {
        return this.partituraService.listar(musicaId);
    }

    @DeleteMapping("/partituras/{id}")
    @Operation(summary = "Remover partitura", description = "Remove uma partitura e seu arquivo associado.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'MAESTRO')")
    public ResponseEntity<Void> removerPartitura(@PathVariable UUID id) {
        this.partituraService.remover(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/partituras/{id}")
    @Operation(summary = "Atualizar partitura", description = "Atualiza os metadados de uma partitura.")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'MAESTRO')")
    public PartituraResponse atualizarPartitura(
            @PathVariable UUID id,
            @RequestBody @Valid PartituraRequest request
    ) {
        return this.partituraService.atualizar(id, request);
    }



}
