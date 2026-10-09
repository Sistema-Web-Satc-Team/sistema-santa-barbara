package br.org.bandasantabarbara.application.dtos.instrumentos;

import br.org.bandasantabarbara.model.Instrumento;
import io.swagger.v3.oas.annotations.media.Schema;

public record InstrumentoResponse(


        @Schema(
                description = "Id do instrumento."
        )
        int id,


        @Schema(
                description = "Nome do instrumento."
        )
        String nome,

        @Schema(
                description = "Descrição do instrumento."
        )
        String descricao,

        @Schema(
                description = "URL pública da imagem do instrumento."
        )
        String urlImagem,

        @Schema(
                description = "Tipo do instrumento"
        )
        Instrumento.TipoInstrumentoEnum tipo

) {

}
