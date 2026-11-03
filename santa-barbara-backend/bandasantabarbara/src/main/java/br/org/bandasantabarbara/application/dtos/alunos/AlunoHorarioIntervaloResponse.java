package br.org.bandasantabarbara.application.dtos.alunos;

import java.time.LocalTime;

public record AlunoHorarioIntervaloResponse(
    String diaDaSemana,
    LocalTime inicio,
    LocalTime fim
) {
}
