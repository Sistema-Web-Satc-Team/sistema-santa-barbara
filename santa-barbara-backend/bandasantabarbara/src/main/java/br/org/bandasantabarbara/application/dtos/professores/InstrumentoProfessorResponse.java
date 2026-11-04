package br.org.bandasantabarbara.application.dtos.professores;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Instrumentos que o professor está ensinando em turmas ativas.")
public record InstrumentoProfessorResponse(

        @Schema(
            description = "Nomes dos instrumentos ensinados pelo professor."
        )
        List<String> ensinando

) {
    public InstrumentoProfessorResponse {
        ensinando = ensinando == null ? List.of() : List.copyOf(ensinando);
    }
}
