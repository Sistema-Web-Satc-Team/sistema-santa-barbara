package br.org.bandasantabarbara.application;

import java.util.List;

public record DomainExceptionResponse (
    String message,
    List<String> errors
) {}
