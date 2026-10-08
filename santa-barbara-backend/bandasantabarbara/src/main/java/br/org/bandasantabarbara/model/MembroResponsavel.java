package br.org.bandasantabarbara.model;

import br.org.bandasantabarbara.exception.EnumBadRequestException;
import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "membro_responsavel")
public class MembroResponsavel {

    @EmbeddedId
    private MembroResponsavelId id;

    @ManyToOne
    @MapsId("idResponsavel")
    @JoinColumn(name = "id_responsavel")
    private Membro responsavel;

    @ManyToOne
    @MapsId("idMembroMenor")
    @JoinColumn(name = "id_membro_menor")
    private Membro membroMenor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_relacao")
    @Getter @Setter
    private TipoRelacaoResponsavel tipoRelacao;

    public enum TipoRelacaoResponsavel {
        PROGENITORES,
        AVO,
        PADRINHO,
        TIO,
        RESPONSAVEL_LEGAL;

        @JsonCreator
        public static MembroResponsavel.TipoRelacaoResponsavel fromString(String value) {
            if (value == null || value.isBlank()) {
                return null;
            }

            String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                    .trim()
                    .replaceAll("\\s+", "_")
                    .toUpperCase();

            for (MembroResponsavel.TipoRelacaoResponsavel tipo : MembroResponsavel.TipoRelacaoResponsavel.values()) {
                if (tipo.name().equalsIgnoreCase(normalized)) {
                    return tipo;
                }
            }

            List<String> values = new ArrayList<>();

            values.add("Progenitores");
            values.add("Avo");
            values.add("Tio");
            values.add("Responsavel Legal");

            throw new EnumBadRequestException("Relação informada é inválido:", values);
        }
    }


}

