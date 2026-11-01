package br.org.bandasantabarbara.model.turma;

import br.org.bandasantabarbara.model.ensino.Aluno;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "matricula_aluno_turma")
@Getter
@Setter
public class MatriculaAlunoTurma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_turma", nullable = false)
    private Turma turma;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_aluno", nullable = false)
    private Aluno aluno;

    @Column(nullable = false)
    private String status = "ATIVO";

    @Column(name = "justificativa_mudanca_status", columnDefinition = "TEXT")
    private String justificativaMudancaStatus;

    @Column(name = "data_matricula", nullable = false)
    private LocalDate dataMatricula;

    @Column(name = "data_saida")
    private LocalDate dataSaida;

    protected MatriculaAlunoTurma() {
    }

    public MatriculaAlunoTurma(Turma turma, Aluno aluno, LocalDate dataMatricula) {
        this.turma = turma;
        this.aluno = aluno;
        this.dataMatricula = dataMatricula;
    }
}
