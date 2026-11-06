package br.org.bandasantabarbara.application.dtos.instrumentos;

import org.hibernate.validator.constraints.Length;

import br.org.bandasantabarbara.model.musica.Instrumento;
import io.swagger.v3.oas.annotations.media.Schema;

public record AtualizarInstrumentoRequest (

    @Length(min = 2, message = "Nome deve ter pelo menos 2 caracteres.")
    @Length(max = 64, message = "Limite de caracteres excedido.")
    @Schema(description = "Nome do instrumento.", example = "flauta", minLength = 2, maxLength = 64, requiredMode = Schema.RequiredMode.REQUIRED)
    String nome,

    @Schema(
            description = "Família do instrumento.",
            example = "SOPRO",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    Instrumento.TipoInstrumentoEnum tipo,

    @Length(min = 2, message = "A descrição deve ter pelo menos 2 caracteres.")
    @Schema(description = "Descrição opcional do instrumento.", example = "Instrumento de sopro.")
    String descricao
) {}
