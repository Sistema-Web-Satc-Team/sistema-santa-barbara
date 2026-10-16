package br.org.bandasantabarbara.application.dtos.partituras;

import java.util.List;

public record PartituraRequest(
        String titulo,
        String arranjador,
        List<String> instrumentos
) {
}
