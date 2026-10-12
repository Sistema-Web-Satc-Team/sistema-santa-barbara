package br.org.bandasantabarbara.application.dtos.files;

import java.util.UUID;

public record FileResponse(
        UUID id,
        String filename,
        String contentType
) {}