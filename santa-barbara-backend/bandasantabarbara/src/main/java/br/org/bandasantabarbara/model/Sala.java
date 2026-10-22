package br.org.bandasantabarbara.model;

import br.org.bandasantabarbara.exception.BadRequestException;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "sala")
public class Sala {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Integer id;

    @Getter
    private String code;

    @Getter
    private Integer capacidade;

    protected  Sala() {

    }

    public Sala(String code, int capacidade) {
        this.setCode(code);
        this.setCapacidade(capacidade);
    }

    public void setCode(String code) {
        if (code == null || code.isBlank()) {
            throw new BadRequestException("O código não pode estar vazio.");
        }

        this.code = code;
    }


    public void setCapacidade(Integer capacidade) {
        if (capacidade == null) {
            throw new BadRequestException("A capacidade da sala não pode ser nula.");
        }

        if (capacidade <= 0) {
            throw new BadRequestException("A capacidade da sala deve ser um número positivo maior que zero.");
        }

        this.capacidade = capacidade;
    }
}
