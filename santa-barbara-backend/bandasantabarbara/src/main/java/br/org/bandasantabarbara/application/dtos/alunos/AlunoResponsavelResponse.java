package br.org.bandasantabarbara.application.dtos.alunos;

import java.util.UUID;

public record AlunoResponsavelResponse(
        UUID id,
        String nome,
        String email,
        String telefone,
        String relacao
) {
}
