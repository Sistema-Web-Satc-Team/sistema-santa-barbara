package br.org.bandasantabarbara.model.membro;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class MembroResponsavelId implements Serializable {

    @Column(name = "id_membro_responsavel")
    @Setter @Getter
    private UUID idMembroResponsavel;

    @Column(name = "id_membro_dependente")
    @Setter @Getter
    private UUID idMembroDependente;

}