package br.org.bandasantabarbara.application.dtos.turmas;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Professor com vínculo ativo em uma turma.")
public record TurmaProfessorResponseDTO(
    Long idProfessorTurma,
    UUID idProfessor,
    String nome,
    String telefone,
    String email
) {}
