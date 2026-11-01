package br.org.bandasantabarbara.application.dtos.turmas;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Registro histórico de vínculo de professor com turma.")
public record ProfessorTurmaResponseDTO(
    Long id,
    UUID idProfessor,
    Long idTurma,
    String status,
    LocalDate dataEntrada,
    LocalDate dataSaida
) {
}
