package br.org.bandasantabarbara.application.dtos;

public record OffsetPaginationRequest(
        int page,
        int size
) {
    public OffsetPaginationRequest {
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 10; // Evita queries maliciosas com size gigante
    }
}