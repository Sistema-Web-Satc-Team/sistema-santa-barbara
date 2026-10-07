package br.org.bandasantabarbara.application.dtos.exception;

import java.util.List;

public record EnumBadRequestExceptionDTO(
        String message,
        List<String> validValues
) {}
