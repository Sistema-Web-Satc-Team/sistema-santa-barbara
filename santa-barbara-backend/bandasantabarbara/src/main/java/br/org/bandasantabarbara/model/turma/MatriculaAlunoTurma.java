package br.org.bandasantabarbara.model.turma;

import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.model.ensino.Aluno;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Table(name = "matricula_aluno_turma")
@Getter
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

    public boolean estaAtiva() {
        return "ATIVO".equals(status);
    }

    public void desistir(LocalDate dataSaida) {
        encerrar("DESISTENTE", dataSaida);
    }

    public void encerrarPorTerminoDaTurma(LocalDate dataSaida) {
        encerrar("ENCERRADO", dataSaida);
    }

    private void encerrar(String novoStatus, LocalDate dataSaida) {
        if (!estaAtiva()) {
            throw new BadRequestException("A matrícula não está ativa.");
        }
        if (dataSaida.isBefore(dataMatricula)) {
            throw new BadRequestException("A data de saída não pode ser anterior à matrícula.");
        }
        this.status = novoStatus;
        this.dataSaida = dataSaida;
    }
}
