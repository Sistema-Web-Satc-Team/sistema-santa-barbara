package br.org.bandasantabarbara.application.dtos.exception;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detalhes de uma requisição inválida com erros associados.")
public record BadRequestExceptionDTO(
    @Schema(description = "Descrição geral do problema.")
    String message,
    @Schema(description = "Lista de erros encontrados.")
    List<String> errors
) {}
