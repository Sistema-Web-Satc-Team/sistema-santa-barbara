package br.org.bandasantabarbara.application.dtos.files;

import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Metadados de um arquivo armazenado.")
public record FileResponse(
        @Schema(description = "Identificador do arquivo.", format = "uuid")
        UUID id,
        @Schema(description = "Nome original do arquivo.", example = "documento.pdf")
        String filename,
        @Schema(description = "Tipo MIME do arquivo.", example = "application/pdf")
        String contentType
) {}