package br.org.bandasantabarbara.application.dtos.exception;

import java.util.List;

public record BadRequestExceptionDTO(
    String message,
    List<String> errors
) {}
