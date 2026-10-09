package br.org.bandasantabarbara.model;


import br.org.bandasantabarbara.exception.BadRequestException;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Locale;

@Entity
@Table(name = "instrumento")
public class Instrumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Getter
    private TipoInstrumentoEnum tipo;


    @Column(name = "descricao")
    private String descricao;


    protected Instrumento() { }

    public Instrumento(String nome, Instrumento.TipoInstrumentoEnum tipo) {
        this.setNome(nome);
        this.setTipo(tipo);
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new BadRequestException("O nome não pode ser nulo ou em branco.");
        }

        this.nome = nome.trim().toLowerCase();
    }

    public void setDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new BadRequestException("O descricao não pode ser nulo ou em branco.");
        }

        this.descricao = descricao.trim().toLowerCase();
    }

    public void setTipo(Instrumento.TipoInstrumentoEnum tipo) {
        if (tipo == null) {
            throw new BadRequestException("O tipo não pode ser nulo ou em branco.");
        }

        this.tipo = tipo;
    }
}
