package br.org.bandasantabarbara.model;

import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.exception.ExpiredException;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "convite")
public class Convite {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_membro")
    private Membro membro;

    @Getter
    @Column(name = "data_convite_criacao", nullable = false)
    private Instant dataCriacaoConvite;

    @Getter
    @Column(name = "data_convite_expiracao", nullable = false)
    private Instant dataExpiracaoConvite;

    @Column(nullable = false, columnDefinition = "status_convite")
    @Enumerated(EnumType.STRING)
    @Getter
    private EnumConviteStatus status;


    protected Convite() {
        this.dataCriacaoConvite = Instant.now();
        this.dataExpiracaoConvite = this.dataCriacaoConvite.plus(7, ChronoUnit.DAYS);
    }

    public Convite(Membro membro) {
        this();
        this.membro = membro;
    }

    public boolean foiEnviado() {
        return this.status == EnumConviteStatus.ENVIADO
                || this.status == EnumConviteStatus.REENVIADO;
    }

    public boolean estaExpirado() {
        if (this.status == EnumConviteStatus.EXPIRADO) {
            return true;
        }

        if (this.dataExpiracaoConvite.isBefore(Instant.now())) {
            this.status = EnumConviteStatus.EXPIRADO;
            return  true;
        }

        return false;
    }

    public void verificarSePodeSerAceito() {
        if (this.status == EnumConviteStatus.ACEITO) {
            throw new BadRequestException("Este convite já foi aceito.");
        }

        if (estaExpirado()) {
            throw new ExpiredException("Este convite já expirou.");
        }

        if (!foiEnviado()) {
            throw new ExpiredException("Este convite não está mais disponível para aceite.");
        }
    }

    public boolean podeSerAceito() {
        if (this.status == EnumConviteStatus.ACEITO) {
            return false;
        }

        if (estaExpirado()) {
            return false;
        }

        if (!foiEnviado()) {
            return false;
        }

        return true;
    }

    public void verificarSePodeSerEnviado() {
        if (this.status == EnumConviteStatus.ACEITO) {
            throw new BadRequestException("O convite não pode ser enviado. Membro já aceitou um convite.");
        }


        if (!estaExpirado()) {
            throw new BadRequestException("Já existe um convite válido enviado ou reenviado.");
        }

    }


    public void verificarSePodeSerReenviado() {
        if (this.status == EnumConviteStatus.ACEITO) {
            throw new BadRequestException("Não é possível reenviar o convite: o membro já aceitou e possui acesso à plataforma.");
        }

        if (estaExpirado()) {
            throw new ExpiredException("Convite expirado. Crie um novo convite.");
        }
    }

    public void registrarEnvioComSucesso() {
        this.status = foiEnviado()
                ? EnumConviteStatus.REENVIADO
                : EnumConviteStatus.ENVIADO;
    }

    public void registrarFalhaEnvio() {
        this.status = foiEnviado()
                ? EnumConviteStatus.FALHA_REENVIO
                : EnumConviteStatus.FALHA_ENVIO;
    }


}
