package br.org.bandasantabarbara.model;

import br.org.bandasantabarbara.exception.BadRequestException;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;


//
// Entidade Membro Vinculo
//
// Representa as relações organizacionais
// As funções de um membro são definidos aqui
//

@Entity
@Table(name = "membro_vinculo")
public class MembroVinculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    public int getId() {
        return this.id;
    }


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_funcao", nullable = false)
    @Getter
    private Funcao funcao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_membro", nullable = false)
    @Getter
    private Membro membro;

    @Column(name = "data_inicio", nullable = false)
    @Getter
    private LocalDate dataInicio;

    @Column(name = "data_termino")
    @Getter
    private LocalDate dataTermino;

    @Column(name = "criado_em")
    @Getter
    private Instant criadoEm;

    @Column(name = "atualizado_em")
    @Getter
    private Instant atualizadoEm;

    @Column(name = "deletado_em")
    @Getter
    private Instant deletadoEm;


    public enum MembroVinculoStatusEnum {
            ATIVO,
            AGENDADO,
            CANCELADO,
            ENCERADO;

    }

    // Construtores

    protected  MembroVinculo() { }

    public MembroVinculo(Membro membro, Funcao funcao, LocalDate dataInicio) {
        this.membro = membro;
        this.funcao = funcao;
        this.dataInicio = dataInicio;
        this.criadoEm = Instant.now();
    }

    public void cancelar() {
        if (deletadoEm != null) {
            throw new BadRequestException("O vínculo já está cancelado.");
        }

        if (!this.estaAgendado()) {
            throw new BadRequestException("Apenas vínculos com status AGENDADO podem ser cancelados. Vínculos ativos ou encerrados devem ser encerrados por data.");
        }

        this.deletadoEm = Instant.now();
    }

    public void encerrar() {

        if (this.estaCancelado()) {
            throw new BadRequestException("Um vínculo cancelado não pode ser encerrado.");
        }

        if (this.estaAgendado()) {
            throw new BadRequestException("Vínculos com status AGENDADO não podem ser encerrados. Use a rota de cancelamento.");
        }

        if (this.dataTermino != null) {
            throw new BadRequestException("O vínculo já está encerrado.");
        }

        this.dataTermino = LocalDate.now();
        this.atualizadoEm = Instant.now();
    }


    public boolean estaAtivo() {
        LocalDate hoje = LocalDate.now();

        return !this.dataInicio.isAfter(hoje) && (this.dataTermino == null || this.dataTermino.isAfter(hoje));
    }

    public boolean estaAgendado() {
        if (this.estaCancelado()) return false;

        LocalDate hoje = LocalDate.now();

        return this.dataInicio.isAfter(hoje) && this.dataTermino == null;
    }

    public boolean estaEncerrado() {
        LocalDate hoje = LocalDate.now();

        return this.dataTermino != null && !this.dataTermino.isAfter(hoje);
    }

    public boolean estaCancelado() {
        return this.deletadoEm != null;
    }

    public MembroVinculo.MembroVinculoStatusEnum getStatus() {

        if (this.estaCancelado()) {return MembroVinculoStatusEnum.CANCELADO; }

        if (this.estaAtivo()) { return MembroVinculoStatusEnum.ATIVO; }

        if (this.estaAgendado()) { return MembroVinculoStatusEnum.AGENDADO; }

        return MembroVinculoStatusEnum.ENCERADO;
    }

}
