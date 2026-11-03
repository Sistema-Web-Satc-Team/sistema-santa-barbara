package br.org.bandasantabarbara.repositories.turma;

import br.org.bandasantabarbara.model.turma.Turma.EnumTipoTurno;

public interface AlunoTurmaProjection {
    Long getTurmaId();

    String getTurma();

    String getProfessor();

    String getSala();

    EnumTipoTurno getTurno();
}
