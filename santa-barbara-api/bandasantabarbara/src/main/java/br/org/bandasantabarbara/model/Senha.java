package br.org.bandasantabarbara.model;

import br.org.bandasantabarbara.exception.DomainException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Senha {

    private final String valor;

    private Senha(String valor) {
        this.valor = valor;
    }

    public String valor() {
        return  this.valor;
    }

    public static Senha criar(String senhaExternal) {
        List<String> errors = new ArrayList<>();

        var senha = senhaExternal.trim();

        if (senha.length() < 8) {
            errors.add("A senha deve possuir no mínimo 8 caracteres.");
        }

        if (!senha.matches(".*[A-Z].*")) {
            errors.add("A senha deve possuir pelo menos uma letra maiúscula.");
        }

        if (!senha.matches(".*\\d.*")) {
            errors.add("A senha deve possuir pelo menos um número.");
        }

        if (!senha.matches(".*[-#@_].*")) {
            errors.add("A senha deve possuir pelo menos um símbolo especial (-, #, @ ou _).");
        }

        if (!errors.isEmpty()) {
            throw  new DomainException("Senha inválida", errors);
        }

        return new Senha(senha);
    }
}
