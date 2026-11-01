package br.org.bandasantabarbara.application.dtos.exception;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Erro de validação de enum com os valores aceitos.")
public record EnumBadRequestExceptionDTO(
        @Schema(description = "Descrição do problema.")
        String message,
        @Schema(description = "Valores válidos para o campo.")
        List<String> validValues
) {}
