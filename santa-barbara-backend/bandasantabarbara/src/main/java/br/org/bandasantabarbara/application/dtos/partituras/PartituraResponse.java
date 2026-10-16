package br.org.bandasantabarbara.application.dtos.partituras;

import br.org.bandasantabarbara.application.dtos.instrumentos.InstrumentoResponse;

import java.util.List;
import java.util.UUID;

public record PartituraResponse (
        UUID id,
        String titulo,
        String arranjador,
        String partituraUrl,
        List<InstrumentoResponse> instrumentos
) {
}
