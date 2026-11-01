package br.org.bandasantabarbara.model.turma;

import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.model.ensino.Professor;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Table(name = "professor_turma")
@Getter
public class ProfessorTurma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_turma", nullable = false)
    private Turma turma;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_professor", nullable = false)
    private Professor professor;

    @Column(nullable = false)
    private String status = "ATIVO";

    @Column(name = "justificativa_mudanca_status", columnDefinition = "TEXT")
    private String justificativaMudancaStatus;

    @Column(name = "data_entrada", nullable = false)
    private LocalDate dataEntrada;

    @Column(name = "data_saida")
    private LocalDate dataSaida;

    protected ProfessorTurma() {
    }

    public ProfessorTurma(Turma turma, Professor professor, LocalDate dataEntrada) {
        this.turma = turma;
        this.professor = professor;
        this.dataEntrada = dataEntrada;
    }

    public void encerrarPorTerminoDaTurma(LocalDate dataSaida) {
        if (!"ATIVO".equals(status)) {
            throw new BadRequestException("O vínculo do professor não está ativo.");
        }
        if (dataSaida.isBefore(dataEntrada)) {
            throw new BadRequestException("A data de saída não pode ser anterior à entrada.");
        }
        this.status = "ENCERRADO";
        this.dataSaida = dataSaida;
    }
}
