package br.org.bandasantabarbara.application.dtos.alunos;

import java.util.List;
import java.util.UUID;

import br.org.bandasantabarbara.model.membro.Membro;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de um aluno, incluindo seus responsáveis e instrumentos.")
public record AlunoResponse(
        @Schema(description = "Identificador do aluno.", format = "uuid")
        UUID id,
        @Schema(description = "Nome completo do aluno.")
        String nome,
        @Schema(description = "E-mail do aluno.", format = "email")
        String email,
        @Schema(description = "Telefone do aluno.")
        String telefone,
        @Schema(description = "Sexo do aluno.")
        Membro.MembroSexoEnum sexo,
        @Schema(description = "Idade em anos.", example = "12")
        int idade,
        @Schema(description = "Indica se o aluno possui turma.")
        boolean temTurma,
        @Schema(description = "Instrumentos que o aluno está aprendendo.")
        InstrumentoAlunoResponse instrumentos,
        @Schema(description = "Responsáveis associados ao aluno.")
        List<AlunoResponsavelResponse> responsaveis
) {

    public AlunoResponse {
        instrumentos = instrumentos == null ? new InstrumentoAlunoResponse(List.of()) : instrumentos;
        responsaveis = responsaveis == null ? List.of() : List.copyOf(responsaveis);
    }
}