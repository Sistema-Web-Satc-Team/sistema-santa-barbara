package br.org.bandasantabarbara.model.aula;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChamadaId implements Serializable {

    @Column(name = "id_aula")
    private Long idAula;

    @Column(name = "id_matricula_aluno")
    private Long idMatriculaAluno;
}