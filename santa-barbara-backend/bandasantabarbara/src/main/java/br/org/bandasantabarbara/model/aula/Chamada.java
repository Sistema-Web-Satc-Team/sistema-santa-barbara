package br.org.bandasantabarbara.model.aula;

import br.org.bandasantabarbara.model.turma.MatriculaAlunoTurma;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity 
@Table(name = "chamada")
public class Chamada {
    
    @EmbeddedId
    private ChamadaId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("idAula")
    @JoinColumn(name = "id_aula", nullable = false)
    @Getter
    @Setter
    private Aula aula;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("idMatriculaAluno")
    @JoinColumn(name = "id_matricula_aluno", nullable = false)
    @Getter
    @Setter
    private MatriculaAlunoTurma matriculaAlunoTurma;

    @Getter @Setter
    @Column(name = "presente")
    private boolean presente;

    protected Chamada() { }

    public Chamada(Aula aula, MatriculaAlunoTurma matriculaAlunoTurma, boolean presente) {
        this.id = new ChamadaId(aula.getId(), matriculaAlunoTurma.getId());
        this.aula = aula;
        this.matriculaAlunoTurma = matriculaAlunoTurma;
        this.presente = presente;
    }

    public void atualizarPresenca(boolean presente) {
        this.presente = presente;
    }

}
