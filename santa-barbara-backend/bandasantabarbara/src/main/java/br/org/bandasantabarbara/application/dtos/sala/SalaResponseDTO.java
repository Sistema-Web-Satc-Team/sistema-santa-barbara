package br.org.bandasantabarbara.application.dtos.sala;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representa os dados de uma sala física cadastrada.")
public record SalaResponseDTO(

    @Schema(
        description = "Identificador da sala.",
        example = "1"
    )
    Integer id,

    @Schema(
        description = "Código identificador da sala.",
        example = "1.10"
    )
    String code,

    @Schema(
        description = "Capacidade máxima de pessoas.",
        example = "12"
    )
    Integer capacidade

) {}