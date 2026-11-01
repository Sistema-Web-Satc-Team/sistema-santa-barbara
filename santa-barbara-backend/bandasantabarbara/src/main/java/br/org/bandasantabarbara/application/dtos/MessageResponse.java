package br.org.bandasantabarbara.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Mensagem simples retornada pela API.")
public record MessageResponse(
        @Schema(description = "Texto informativo da resposta.", example = "Operação realizada com sucesso.")
        String message
) {
}
