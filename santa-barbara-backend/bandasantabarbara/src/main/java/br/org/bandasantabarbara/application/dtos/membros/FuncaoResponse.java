package br.org.bandasantabarbara.application.dtos.membros;

import io.swagger.v3.oas.annotations.media.Schema;

public record FuncaoResponse(

        @Schema(
                description = "Código da função.",
                example = "MAESTRO"
        )
        String code,

        @Schema(
                description = "Nome da função.",
                example = "Maestro"
        )
        String nome,

        @Schema(
                description = "Descrição da função.",
                example = "Responsável pela direção musical da banda."
        )
        String descricao
) {}