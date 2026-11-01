package br.org.bandasantabarbara.application.dtos;
import org.springframework.data.domain.Page;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Resultado paginado de uma consulta.")
public record PageResponse<T>(
        @Schema(description = "Itens da página atual.")
        List<T> content,
        @Schema(description = "Índice da página atual, começando em zero.", example = "0")
        int page,
        @Schema(description = "Quantidade máxima de itens por página.", example = "20")
        int size,
        @Schema(description = "Quantidade total de itens disponíveis.", example = "125")
        long totalElements,
        @Schema(description = "Quantidade total de páginas.", example = "7")
        int totalPages
) {}