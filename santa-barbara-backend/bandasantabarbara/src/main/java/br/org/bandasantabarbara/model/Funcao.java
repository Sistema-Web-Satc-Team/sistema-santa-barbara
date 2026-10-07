package br.org.bandasantabarbara.model;

import br.org.bandasantabarbara.exception.BadRequestException;
import jakarta.persistence.*;
import lombok.Getter;


@Entity
@Table(name = "funcao")
public class Funcao {

    public enum TipoFuncaoEnum {
        DOMINIO,
        SISTEMA
    }

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Getter
    private int id;

    @Column(name = "code", length = 32, nullable = false)
    @Getter
    private String code;

    @Column(name = "nome", length = 64, nullable = false)
    @Getter
    private String nome;

    @Column
    @Getter
    private String descricao;


    @Column(nullable = false, columnDefinition = "tipo_funcao")
    @Enumerated(EnumType.STRING)
    @Getter
    private TipoFuncaoEnum tipo;


    // Construtor da classe

    protected  Funcao() {

    }

    public Funcao(String code, String nome, TipoFuncaoEnum tipo) {

        validateNome(nome);
        validateCode(code);

        this.code = normalizeCode(code);
        this.nome = normalizeNome(nome);
        this.tipo = tipo;

    }

    // Validations

    private void validateCode(String code) {
        if (code == null || code.isBlank()) {
            throw new BadRequestException("Código da função não pode estar vazio.");
        }
    }

    private void validateNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new BadRequestException("Nome da função não pode estar vazio.");
        }
    }

    private void validateDescricao(String descricao) {
        if (descricao != null && descricao.isBlank()) {
            throw new BadRequestException("Descrição da função foi preenchida, no entanto esta vazio.");
        }
    }

    // Normalizations

    private String normalizeNome(String nome) {
        nome = nome.trim().toLowerCase();

        return Character.toUpperCase(nome.charAt(0))
                + nome.substring(1);
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase();
    }


    // Setters

    public void setCode(String code) {
        validateCode(code);
        this.code = normalizeCode(code);
    }

    public void setNome(String nome) {
        validateNome(nome);
        this.nome = normalizeNome(nome);
    }

    public void setDescricao(String descricao) {
        validateDescricao(descricao);
        this.descricao = descricao;
    }

}
