package br.org.bandasantabarbara.application.dtos.professores;

import br.org.bandasantabarbara.model.turma.Turma.EnumTipoTurno;
import java.util.List;

public record ProfessorTurmaAtualResponse(
    String turma,
    String professor,
    String sala,
    EnumTipoTurno turno,
    List<String> alunos,
    List<String> instrumentos
) {
}
