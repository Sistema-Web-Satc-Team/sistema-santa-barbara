package br.org.bandasantabarbara.application.dtos.professores;

import java.util.List;
import java.util.UUID;

import br.org.bandasantabarbara.model.membro.Membro;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de contato, atuação e alocação de um professor.")
public record ProfessorResponse(
        @Schema(
            description = "Identificador do professor.", 
            format = "uuid"
        )
        UUID id,

        @Schema(
            description = "Nome completo do professor."
        )
        String nome,

        @Schema(
            description = "E-mail do professor.",
            format = "email"
        )
        String email,

        @Schema(
            description = "Telefone do professor."
        )
        String telefone,

        @Schema(
            description = "Sexo do professor."
        )
        Membro.MembroSexoEnum sexo,

        @Schema(
            description = "Indica se o professor está alocado em uma turma ativa."
        )
        boolean temTurma,

        @Schema(
            description = "Instrumentos ensinados pelo professor em turmas ativas."
        )
        InstrumentoProfessorResponse instrumentos
) {
    public ProfessorResponse {
        instrumentos = instrumentos == null ? new InstrumentoProfessorResponse(List.of()) : instrumentos;
    }
}
