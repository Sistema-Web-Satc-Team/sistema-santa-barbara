package br.org.bandasantabarbara.application.dtos;

import java.util.List;

public record DomainExceptionResponse (
    String message,
    List<String> errors
) {}
