package br.org.bandasantabarbara.model.musica;


import java.time.LocalDateTime;

import br.org.bandasantabarbara.exception.BadRequestException;
import jakarta.persistence.*;
import lombok.Getter;


@Entity
@Table(name = "instrumento")
public class Instrumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private int id;


    @Column(name = "nome")
    @Getter
    private String nome;

    public enum TipoInstrumentoEnum {
        SOPRO,
        CORDA,
        TECLAS,
        PERCUSSAO
    }


    @Column(name = "tipo", nullable = false)
    @Enumerated(EnumType.STRING)
    @Getter
    private TipoInstrumentoEnum tipo;


    @Column(name = "descricao")
    @Getter
    private String descricao;

    @Column(name = "criado_em", nullable = false, updatable = false)
    @Getter
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false, updatable = true)
    @Getter
    private LocalDateTime atualizadoEm;



    protected Instrumento() { 
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    public Instrumento(String nome, Instrumento.TipoInstrumentoEnum tipo) {
        this();
        this.setNome(nome);
        this.setTipo(tipo);
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new BadRequestException("O nome não pode ser nulo ou em branco.");
        }

        this.nome = nome.trim().toLowerCase();
        this.atualizadoEm = LocalDateTime.now();
    }

    public void setDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new BadRequestException("O descricao não pode ser nulo ou em branco.");
        }

        this.descricao = descricao.trim().toLowerCase();
        this.atualizadoEm = LocalDateTime.now();
    }

    public void setTipo(Instrumento.TipoInstrumentoEnum tipo) {
        if (tipo == null) {
            throw new BadRequestException("O tipo não pode ser nulo ou em branco.");
        }

        this.tipo = tipo;
        this.atualizadoEm = LocalDateTime.now();
    }
}
