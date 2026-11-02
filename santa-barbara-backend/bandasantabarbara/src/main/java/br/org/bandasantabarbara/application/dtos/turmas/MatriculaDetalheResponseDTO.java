package br.org.bandasantabarbara.application.dtos.turmas;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Dados detalhados de uma matrícula de aluno em turma.")
public record MatriculaDetalheResponseDTO(
    Long idMatricula,
    Long idTurma,
    UUID idAluno,
    String nome,
    String telefone,
    String email,
    boolean ehMenor,
    String status,
    String justificativa
) {}
