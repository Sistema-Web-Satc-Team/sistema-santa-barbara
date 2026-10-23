package br.org.bandasantabarbara.model;

import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.repositories.StatusTurma;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.time.LocalDate;

@Entity
@Table(name = "turma")
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column
    @Getter
    private String nome;

    @ManyToOne
    @JoinColumn(name = "sala_id")
    @Getter @Setter
    private Sala sala;

    @OneToOne
    @JoinColumn(name = "horario_id")
    @Getter @Setter
    private Horario horario;

    @Column(name = "data_inicio")
    @Getter
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    @Getter
    private LocalDate dataFim;

    @Column(name = "data_termino")
    @Getter
    private LocalDate dataTermino;

    // Debito-05
    // Acoplamento da entidade com hibernate
    @Formula("""
        CASE 
            WHEN data_termino IS NOT NULL AND data_termino < data_inicio THEN 'CANCELADO'
            WHEN data_termino IS NOT NULL AND data_termino < data_fim THEN 'INTERROMPIDA'
            WHEN data_termino IS NOT NULL THEN 'ENCERRADO'
            WHEN data_inicio IS NULL OR CURRENT_DATE < data_inicio THEN 'PLANEJAMENTO'
            WHEN data_fim IS NOT NULL AND CURRENT_DATE >= data_fim THEN 'ENCERRADO'
            ELSE 'EM_ANDAMENTO'
        END
    """)
    @Enumerated(EnumType.STRING)
    private StatusTurma status;

    protected Turma() {

    }

    public Turma(String nome, Sala sala) {
        this.nome = nome;
        this.sala = sala;
    }


    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw  new BadRequestException("Nome não pode estar vazio ou em branco.");
        }

        this.nome = nome.toLowerCase().trim();
    }

    public void agendarPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio != null && inicio.isBefore(LocalDate.now())) {
            throw new BadRequestException("A data de início não pode ser no passado.");
        }

        if (inicio == null && fim != null) {
            throw new BadRequestException("Não é possível definir uma data de fim sem ter um inicio.");
        }

        if (inicio != null && fim != null && inicio.isAfter(fim)) {
            throw new BadRequestException("A data de fim deve ocorrer após a data de inicio.");
        }

        this.dataInicio = inicio;
        this.dataFim = fim;
    }

    public void terminar() {
        if (this.dataTermino != null) {
            throw new BadRequestException("Não é possível terminar uma turma já terminou.");
        }
        this.dataTermino = LocalDate.now();
    }

    public StatusTurma getStatus() {
        LocalDate hoje = LocalDate.now();

        if (this.dataTermino != null) {
            if (this.dataTermino.isBefore(this.dataInicio)) {
                return StatusTurma.CANCELADO;
            }

            if (this.dataFim != null && this.dataTermino.isBefore(this.dataFim)) {
                return StatusTurma.INTERROMPIDA;
            }

            return StatusTurma.ENCERRADO;
        }

        if (this.dataInicio == null || hoje.isBefore(this.dataInicio)) {
            return StatusTurma.PLANEJAMENTO;
        }

        if (this.dataFim != null && !hoje.isBefore(this.dataFim)) {
            return StatusTurma.ENCERRADO;
        }

        return StatusTurma.EM_ANDAMENTO;
    }



}
