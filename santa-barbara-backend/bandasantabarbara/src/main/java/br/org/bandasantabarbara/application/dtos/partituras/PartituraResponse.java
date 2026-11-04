package br.org.bandasantabarbara.application.dtos.partituras;

import br.org.bandasantabarbara.application.dtos.instrumentos.InstrumentoResponse;

import java.util.List;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de uma partitura e do arquivo associado.")
public record PartituraResponse (
        @Schema(description = "Identificador da partitura.", format = "uuid")
        UUID id,
        @Schema(description = "Título da partitura.")
        String titulo,
        @Schema(description = "Nome do arranjador.")
        String arranjador,
        @Schema(description = "URL para baixar a partitura.", format = "uri")
        String partituraUrl,
        @Schema(description = "Instrumentos associados à partitura.")
        List<InstrumentoResponse> instrumentos
) {
}
