package br.org.bandasantabarbara.application.dtos.alunos;

import br.org.bandasantabarbara.model.turma.Turma.EnumTipoTurno;
import java.util.List;

public record AlunoTurmaHorarioResponse(
    String turma,
    String professor,
    String sala,
    EnumTipoTurno turno,
    List<AlunoHorarioIntervaloResponse> horarios
) {
}
