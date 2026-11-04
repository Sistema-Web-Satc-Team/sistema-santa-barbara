package br.org.bandasantabarbara.application.dtos.professores;

import br.org.bandasantabarbara.model.turma.Turma.EnumTipoTurno;
import java.util.List;

public record ProfessorTurmaHorarioResponse(
    String turma,
    String professor,
    String sala,
    EnumTipoTurno turno,
    List<ProfessorHorarioIntervaloResponse> horarios
) {
}
