package br.org.bandasantabarbara.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class MembroResponsavelId implements Serializable {

    @Column(name = "id_responsavel")
    private UUID idResponsavel;

    @Column(name = "id_membro_menor")
    private UUID idMembroMenor;

}