package br.org.bandasantabarbara.application.dtos.aulas;

import br.org.bandasantabarbara.application.dtos.PageResponse;

public record AulaListaResponse(
    String nomeTurma,
    String professor,
    long quantidadeAlunos,
    PageResponse<AulaResponse> aulas
) {}
