package br.org.bandasantabarbara.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class MembroResponsavelId implements Serializable {

    @Column(name = "id_responsavel")
    @Setter @Getter
    private UUID idResponsavel;

    @Column(name = "id_membro_menor")
    @Setter @Getter
    private UUID idMembroMenor;

}