package br.org.bandasantabarbara.application.dtos.alunos;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Instrumentos que o aluno está aprendendo.")
public record InstrumentoAlunoResponse(
        @Schema(description = "Nomes dos instrumentos em aprendizagem.")
        List<String> aprendendo
) {

    public InstrumentoAlunoResponse {
        aprendendo = aprendendo == null ? List.of() : List.copyOf(aprendendo);
    }
}
