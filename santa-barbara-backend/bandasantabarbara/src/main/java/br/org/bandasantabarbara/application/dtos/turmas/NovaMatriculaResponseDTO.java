package br.org.bandasantabarbara.application.dtos.turmas;

import java.util.UUID;

public record NovaMatriculaResponseDTO(
    Long idNovaMatricula,
    UUID idAluno,
    Long idTurma,
    String status,
    String justificativa
) {}
