package br.org.bandasantabarbara.controller;

import br.org.bandasantabarbara.application.usecase.partitura.PegarArquivoPartituraUseCase;
import br.org.bandasantabarbara.application.services.storage.FileService; // se precisar dos metadados do arquivo
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
public class PartituraFileController {

    private final PegarArquivoPartituraUseCase pegarArquivoPartituraUseCase;
    private final FileService fileService;

    public PartituraFileController(
            PegarArquivoPartituraUseCase pegarArquivoPartituraUseCase,
            FileService fileService
    ) {
        this.pegarArquivoPartituraUseCase = pegarArquivoPartituraUseCase;
        this.fileService = fileService;
    }

    @GetMapping("/files/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'MAESTRO', 'ALUNO', 'MUSICO_BANDA', 'PROFESSOR')")
    public ResponseEntity<Resource> download(@PathVariable UUID id) {


        var fileMetadata = this.fileService.getMetadata(id);
        var inputStream = this.pegarArquivoPartituraUseCase.open(id);

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