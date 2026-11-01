package br.org.bandasantabarbara.application.dtos.turmas;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Registro histórico de matrícula de aluno em turma.")
public record MatriculaResponseDTO(
    Long id,
    UUID idAluno,
    Long idTurma,
    String status,
    LocalDate dataMatricula,
    LocalDate dataSaida
) {
}
