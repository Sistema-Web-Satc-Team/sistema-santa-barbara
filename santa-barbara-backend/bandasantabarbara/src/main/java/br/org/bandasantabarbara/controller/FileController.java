package br.org.bandasantabarbara.controller;

import br.org.bandasantabarbara.application.dtos.files.FileResponse;
import br.org.bandasantabarbara.application.services.storage.FileService;
import br.org.bandasantabarbara.model.storage.StoredFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/files")
@Tag(name = "Arquivos", description = "Upload, download e remoção de arquivos.")
public class FileController {

    private final FileService service;

    public FileController(FileService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Enviar arquivo", description = "Envia um arquivo permitido e retorna seus metadados.")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<FileResponse> upload(@RequestParam("file") MultipartFile file) {
        StoredFile saved = service.upload(file);
        URI location = URI.create("/files/" + saved.getId());
        return ResponseEntity.created(location)
                .body(new FileResponse(saved.getId(), saved.getFilename(), saved.getContentType()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Baixar arquivo", description = "Retorna o conteúdo de um arquivo pelo identificador.")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<Resource> download(@PathVariable UUID id) {
        StoredFile file = service.getMetadata(id);

        ContentDisposition disposition = ContentDisposition.inline()
                .filename(file.getFilename(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .header("X-Content-Type-Options", "nosniff")
                .body(new InputStreamResource(service.open(file)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover arquivo", description = "Remove um arquivo pelo identificador.")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}