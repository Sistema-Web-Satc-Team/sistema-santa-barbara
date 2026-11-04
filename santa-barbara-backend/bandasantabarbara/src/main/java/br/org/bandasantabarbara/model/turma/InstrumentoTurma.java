package br.org.bandasantabarbara.model.turma;

import br.org.bandasantabarbara.model.musica.Instrumento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "instrumento_turma")
@Getter
@Setter
public class InstrumentoTurma {

    @EmbeddedId
    private InstrumentoTurmaId id = new InstrumentoTurmaId();

    @MapsId("turmaId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_turma", nullable = false)
    private Turma turma;

    @MapsId("instrumentoId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_instrumento", nullable = false)
    private Instrumento instrumento;

    protected InstrumentoTurma() {
    }

    public InstrumentoTurma(Turma turma, Instrumento instrumento) {
        this.turma = turma;
        this.instrumento = instrumento;
        if (turma.getId() != null && instrumento.getId() > 0) {
            this.id = new InstrumentoTurmaId(turma.getId(), instrumento.getId());
        }
    }
}
