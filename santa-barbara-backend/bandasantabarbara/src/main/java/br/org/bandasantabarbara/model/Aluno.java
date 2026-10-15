package br.org.bandasantabarbara.model;

import jakarta.persistence.*;


@Entity
@Table(name = "aluno")
public class Aluno {

    @Id
    private int id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_vinculo")
    private MembroVinculo vinculo;
}
