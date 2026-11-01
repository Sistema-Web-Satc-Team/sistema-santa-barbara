package br.org.bandasantabarbara.application.dtos.turmas;

import java.time.LocalDate;

import br.org.bandasantabarbara.application.dtos.sala.SalaResponseDTO;
import br.org.bandasantabarbara.model.turma.Turma.EnumTipoTurno;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representa os dados de uma turma cadastrada.")
public record TurmaResponseDTO(

    @Schema(
        description = "Id da turma",
        example = "2"
    )
    Long id,

    @Schema(
        description = "Nome descritivo da turma.",
        example = "Turma de Clarinete"
    )
    String nome,

    @Schema(
        description = "Quantidade máxima de alunos permitida.",
        example = "12"
    )
    Integer numeroVagas,

    @Schema(
        description = "Data planejada para o início das aulas.",
        example = "2026-11-01"
    )
    LocalDate dataInicio,

    @Schema(
        description = "Data planejada para o encerramento das aulas.",
        example = "2026-12-15"
    )
    LocalDate dataFim,

    @Schema(
        description = "Data em que a turma foi efetivamente encerrada ou interrompida. Nulo enquanto não houver término.",
        example = "2026-12-10",
        nullable = true
    )
    LocalDate dataTermino,

    
    @Schema(
        description = "Turno da turma."
    )
    EnumTipoTurno turno,

    @Schema(description = "Horários semanais da turma.")
    HorarioResponseDTO horarios,

    @Schema(description = "Sala física vinculada à turma.")
    SalaResponseDTO sala

) {}