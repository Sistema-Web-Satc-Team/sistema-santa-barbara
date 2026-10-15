package br.org.bandasantabarbara.application.dtos.instrumentos;

import br.org.bandasantabarbara.model.Instrumento;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record InstrumentoRequest(
        @NotBlank
        @Length(min = 2, message = "Nome deve ter pelo menos 2 caracteres.")
        @Length(max = 64, message = "Limite de caracteres excedido.")
        String nome,

        @NotBlank
        @Length(min = 2, message = "O tipo deve ter pelo menos 2 caracteres.")
        @Length(max = 32, message = "Limite de caracteres excedido.")
        Instrumento.TipoInstrumentoEnum tipo,

        @Length(min = 2, message = "A descrição deve ter pelo menos 2 caracteres.")
        String descricao
) { }
