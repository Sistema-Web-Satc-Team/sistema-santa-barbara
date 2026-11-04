package br.org.bandasantabarbara.application.dtos.professores;

import java.time.LocalTime;

public record ProfessorHorarioIntervaloResponse(
    String diaDaSemana,
    LocalTime inicio,
    LocalTime fim
) {
}
