package br.org.bandasantabarbara.exception;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class EnumBadRequestException extends RuntimeException {
    private final List<String> validValues;

    public EnumBadRequestException(String message, List<String> validValues) {
        super(message);
        this.validValues = validValues;
    }
}
