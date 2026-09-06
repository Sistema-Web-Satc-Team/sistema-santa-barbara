package br.org.bandasantabarbara.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Embeddable
public class MembroCredencial {

    @Getter
    @Column(name = "hash_password", nullable = false)
    private String hashSenha;

    @Getter
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @Getter
    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;


    protected MembroCredencial(String hashSenha) {
        this();
        this.hashSenha = hashSenha;
    }

    protected MembroCredencial() {
        this.criadoEm = Instant.now();
        this.atualizadoEm = Instant.now();
    }

    protected void alterarHashSenha(String hashSenha) {
        this.hashSenha = hashSenha;
        this.atualizadoEm = Instant.now();
    }


}
