package br.org.bandasantabarbara.exception;

import lombok.Getter;
import java.util.Collections;
import java.util.List;

@Getter
public class BadRequestException extends RuntimeException {

    private final List<String> errors;

    public BadRequestException(String message) {
        super(message);
        this.errors = Collections.singletonList(message);
    }

    public BadRequestException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }
}