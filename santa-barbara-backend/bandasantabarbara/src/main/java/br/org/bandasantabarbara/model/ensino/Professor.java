package br.org.bandasantabarbara.model.ensino;

import java.util.UUID;

import br.org.bandasantabarbara.model.membro.Membro;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "professor")
public class Professor {
    @Id
    @Column(name = "id_membro")
    @Getter  @Setter
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_membro")
    @Setter @Getter 
    private Membro membro;
}
