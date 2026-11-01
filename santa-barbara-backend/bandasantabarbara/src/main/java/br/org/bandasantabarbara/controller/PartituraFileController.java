package br.org.bandasantabarbara.controller;

import br.org.bandasantabarbara.application.services.musica.PartituraService;
import br.org.bandasantabarbara.application.services.storage.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/partituras")
@Tag(name = "Partituras", description = "Download dos arquivos de partituras.")
public class PartituraFileController {

    private final PartituraService partituraService;
    private final FileService fileService;

    public PartituraFileController(
            PartituraService partituraService,
            FileService fileService
    ) {
        this.partituraService = partituraService;
        this.fileService = fileService;
    }

    @GetMapping("/files/{id}")
    @Operation(summary = "Baixar arquivo de partitura", description = "Retorna o arquivo associado à partitura.")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'MAESTRO', 'ALUNO', 'MUSICO_BANDA', 'PROFESSOR')")
    public ResponseEntity<Resource> download(@PathVariable UUID id) {


        var fileMetadata = this.fileService.getMetadata(id);
        var inputStream = this.partituraService.abrirPartitura(id);

        ContentDisposition disposition = ContentDisposition.inline()
                .filename(fileMetadata.getFilename(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileMetadata.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .header("X-Content-Type-Options", "nosniff")
                .body(new InputStreamResource(inputStream));
    }
}