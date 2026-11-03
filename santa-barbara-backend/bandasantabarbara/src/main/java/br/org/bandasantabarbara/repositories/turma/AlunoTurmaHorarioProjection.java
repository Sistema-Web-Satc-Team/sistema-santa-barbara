package br.org.bandasantabarbara.repositories.turma;

import java.time.DayOfWeek;
import java.time.LocalTime;

public interface AlunoTurmaHorarioProjection {
    Long getTurmaId();

    DayOfWeek getDiaDaSemana();

    LocalTime getInicio();

    LocalTime getFim();
}
