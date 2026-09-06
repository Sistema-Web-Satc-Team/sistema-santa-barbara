package br.org.bandasantabarbara.application.dtos;
import java.util.List;

public record PageResponse<T>(
        List<T> content,
        int pageNumber,
        int pageSize
        // ,
        // long totalElements,
        // int totalPages,
        // boolean isFirst,
        // boolean isLast
) {
    public static <T> PageResponse<T> from(org.springframework.data.domain.Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize()
                // ,
                // page.getTotalElements(),
                // page.getTotalPages(),
                // page.isFirst(),
                // page.isLast()
        );
    }
}