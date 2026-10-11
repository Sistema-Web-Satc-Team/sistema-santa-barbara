package br.org.bandasantabarbara.application.dtos.alunos;

import java.util.List;

public record InstrumentoAlunoResponse(List<String> aprendendo) {

    public InstrumentoAlunoResponse {
        aprendendo = aprendendo == null ? List.of() : List.copyOf(aprendendo);
    }
}
