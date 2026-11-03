package br.org.bandasantabarbara.application.dtos.alunos;

import java.util.List;
import br.org.bandasantabarbara.model.turma.Turma.EnumTipoTurno;

public record AlunoTurmaResponse(
    String turma,
    String professor,
    String sala,
    EnumTipoTurno turno,
    List<String> instrumentos,
    int numeroDeColegas,
    List<String> colegas
) {
    
}
