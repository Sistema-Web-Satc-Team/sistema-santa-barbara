package br.org.bandasantabarbara.application.dtos.aulas;

import java.util.List;

public record AulaChamadasResponse(
    String nomeTurma,
    String professor,
    long quantidadeAlunos,
    AulaResponse aula,
    List<ChamadaResponse> chamadas
) {}
