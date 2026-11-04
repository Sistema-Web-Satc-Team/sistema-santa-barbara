package br.org.bandasantabarbara.application.dtos.alunos;

import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de um responsável associado a um aluno.")
public record AlunoResponsavelResponse(
        @Schema(description = "Identificador do responsável.", format = "uuid")
        UUID id,
        @Schema(description = "Nome completo.")
        String nome,
        @Schema(description = "E-mail de contato.", format = "email")
        String email,
        @Schema(description = "Telefone de contato.")
        String telefone,
        @Schema(description = "Grau de relação com o aluno.", example = "Mãe")
        String relacao
) {
}
