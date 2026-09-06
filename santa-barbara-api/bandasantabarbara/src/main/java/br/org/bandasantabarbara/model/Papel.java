package br.org.bandasantabarbara.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "papel")
public class Papel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private int Id;

    @Getter
    @Setter
    @Column(name = "nome", nullable = false, unique = true)
    private String nome;

    @Getter
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @Getter
    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;


    protected Papel(String nome) {
        criadoEm = Instant.now();
        atualizadoEm = Instant.now();
        this.nome = nome;
    }

    public static Papel superAdmin() {
        return new Papel("SUPER_ADMIN");
    }
}
