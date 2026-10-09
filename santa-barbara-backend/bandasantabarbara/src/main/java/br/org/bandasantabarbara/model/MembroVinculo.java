package br.org.bandasantabarbara.model;

import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.exception.EnumBadRequestException;
import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.*;
import lombok.Getter;

import java.text.Normalizer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


//
// Entidade Membro Vinculo
//
// Representa as relações organizacionais
// As funções de um membro são definidos aqui
//

@Entity
@Table(name = "membro_vinculo")
public class MembroVinculo {

    @EmbeddedId
    private MembroVinculoId id = new MembroVinculoId();

    public int getVinculoId() {
        return this.id.getId();
    }


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_funcao", nullable = false)
    @Getter
    private Funcao funcao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_membro", nullable = false)
    @MapsId("idMembro")
    @Getter
    private Membro membro;

    @Column(name = "data_inicio", nullable = false)
    @Getter
    private Instant dataInicio;

    @Column(name = "data_termino")
    @Getter
    private Instant dataTermino;

    public enum MembroVinculoStatusEnum {
            ATIVO,
            ENCERADO;

    }

    // Construtores

    protected  MembroVinculo() { }

    public MembroVinculo(Membro membro, Funcao funcao) {
        this.membro = membro;
        this.funcao = funcao;
        dataInicio = Instant.now();
    }

    public void encerrar() {

        if (dataTermino != null) {
            throw new BadRequestException("O vínculo já está encerrado.");
        }

        this.dataTermino = Instant.now();
    }

    public void corrigirEncerramento() {
        if (dataTermino == null) {
            throw new BadRequestException("O vínculo não está encerrado.");
        }

        dataTermino = null;
    }


    public boolean estaAtivo() {
        return dataTermino == null;
    }

    public MembroVinculo.MembroVinculoStatusEnum getStatus() {

        if (this.estaAtivo()) { return MembroVinculoStatusEnum.ATIVO; }

        return MembroVinculoStatusEnum.ENCERADO;
    }

}
