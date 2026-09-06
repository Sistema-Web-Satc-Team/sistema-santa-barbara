package br.org.bandasantabarbara.exception;

import lombok.Getter;

import java.util.List;


public class DomainException extends RuntimeException {
    @Getter
    private final List<String> errors;

    public DomainException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    public DomainException(String message) {
        super(message);
        this.errors = List.of();
    }
}
