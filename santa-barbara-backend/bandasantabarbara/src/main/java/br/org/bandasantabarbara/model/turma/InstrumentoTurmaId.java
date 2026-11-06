package br.org.bandasantabarbara.model.turma;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class InstrumentoTurmaId implements Serializable {

    @Column(name = "id_turma")
    private Long turmaId;

    @Column(name = "id_instrumento")
    private Integer instrumentoId;

    public InstrumentoTurmaId() {
    }

    public InstrumentoTurmaId(Long turmaId, Integer instrumentoId) {
        this.turmaId = turmaId;
        this.instrumentoId = instrumentoId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof InstrumentoTurmaId that)) {
            return false;
        }
        return Objects.equals(turmaId, that.turmaId)
                && Objects.equals(instrumentoId, that.instrumentoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turmaId, instrumentoId);
    }
}
