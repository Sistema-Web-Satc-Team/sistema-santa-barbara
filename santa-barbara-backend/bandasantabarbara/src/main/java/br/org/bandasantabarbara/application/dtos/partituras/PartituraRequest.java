package br.org.bandasantabarbara.application.dtos.partituras;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Metadados de uma partitura.")
public record PartituraRequest(
        @Schema(description = "Título da partitura.", example = "Arranjo para flauta")
        String titulo,
        @Schema(description = "Nome do arranjador.", example = "Arranjador")
        String arranjador,
        @Schema(description = "Nomes dos instrumentos para os quais a partitura foi escrita.")
        List<String> instrumentos
) {
}
