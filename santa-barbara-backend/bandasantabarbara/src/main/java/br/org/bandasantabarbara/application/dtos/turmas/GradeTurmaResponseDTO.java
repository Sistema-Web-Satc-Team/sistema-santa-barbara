package br.org.bandasantabarbara.application.dtos.turmas;

import br.org.bandasantabarbara.model.turma.Turma.EnumTipoTurno;
import java.time.LocalDate;

public record GradeTurmaResponseDTO(
    Long id,
    String nome,
    LocalDate dataTermino,
    EnumTipoTurno turno,
    HorarioResponseDTO horarios
) {}
