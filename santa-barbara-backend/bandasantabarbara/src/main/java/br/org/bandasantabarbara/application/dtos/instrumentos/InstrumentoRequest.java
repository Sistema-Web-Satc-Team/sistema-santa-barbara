package br.org.bandasantabarbara.application.dtos.instrumentos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import br.org.bandasantabarbara.model.musica.Instrumento;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para cadastrar ou atualizar um instrumento musical.")
public record InstrumentoRequest(
        @NotBlank
        @Length(min = 2, message = "Nome deve ter pelo menos 2 caracteres.")
        @Length(max = 64, message = "Limite de caracteres excedido.")
        @Schema(description = "Nome do instrumento.", example = "flauta", minLength = 2, maxLength = 64, requiredMode = Schema.RequiredMode.REQUIRED)
        String nome,

        @NotNull(message = "O tipo do instrumento é obrigatório.")
        @Schema(
                description = "Família do instrumento.",
                example = "SOPRO",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Instrumento.TipoInstrumentoEnum tipo,

        @Length(min = 2, message = "A descrição deve ter pelo menos 2 caracteres.")
        @Schema(description = "Descrição opcional do instrumento.", example = "Instrumento de sopro.")
        String descricao
) { }
