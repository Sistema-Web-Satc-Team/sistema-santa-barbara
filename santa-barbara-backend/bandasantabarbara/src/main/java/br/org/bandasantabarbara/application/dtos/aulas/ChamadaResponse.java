package br.org.bandasantabarbara.application.dtos.aulas;

import br.org.bandasantabarbara.model.aula.Chamada;

public record ChamadaResponse(
    Long idMatriculaAluno,
    String nomeAluno,
    boolean presente
) {
    public ChamadaResponse(Chamada chamada) {
        this(
            chamada.getMatriculaAlunoTurma().getId(),
            chamada.getMatriculaAlunoTurma().getAluno().getMembro().getNome(),
            chamada.isPresente()
        );
    }
}
