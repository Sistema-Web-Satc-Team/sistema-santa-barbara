package br.org.bandasantabarbara.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
public class Convite {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    private Membro membro;

    @Getter
    @Column(name = "data_convite_criacao", nullable = false)
    private Instant dataCriacaoConvite;

    @Getter
    @Column(name = "data_convite_expiracao", nullable = false)
    private Instant dataExpiracaoConvite;

    @Getter @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ConviteStatus status;


    protected Convite() {
        this.dataCriacaoConvite = Instant.now();
        this.dataExpiracaoConvite = this.dataCriacaoConvite.plus(7, ChronoUnit.DAYS);
    }

    public Convite(Membro membro) {
        this();
        this.membro = membro;
    }


}
