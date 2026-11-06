package br.org.bandasantabarbara.application.dtos.turmas;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Aluno com matrícula ativa em uma turma.")
public record TurmaAlunoResponseDTO(
    Long idMatricula,
    UUID idAluno,
    String nome,
    String telefone,
    String email,
    boolean ehMenor
) {}
