package br.org.bandasantabarbara.application.dtos.aulas;

import jakarta.validation.constraints.NotNull;

public record ChamadaRequest(
    @NotNull Long idMatriculaAluno,
    @NotNull Boolean presente
) {}
