package br.org.bandasantabarbara.application.dtos.turmas;

import java.io.IOException;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext; // Corrigido o pacote aqui
import com.fasterxml.jackson.databind.JsonDeserializer;

import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.model.turma.Turma.EnumTipoTurno;

public class EnumTipoTurnoDeserializer extends JsonDeserializer<EnumTipoTurno> {
    
    @Override
    public EnumTipoTurno deserialize(
        JsonParser parser,
        DeserializationContext context 
    ) throws IOException {

        String valor = parser.getText()
            .trim()
            .toUpperCase(Locale.ROOT);

        return switch (valor) {
            case "MATUTINO", "MANHA" -> EnumTipoTurno.MATUTINO;
            case "NOTURNO", "NOITE" -> EnumTipoTurno.NOTURNO;
            case "VESPERTINO", "TARDE" -> EnumTipoTurno.VESPERTINO;
            case "INTEGRAL" -> EnumTipoTurno.INTEGRAL;
            case "CONTRATURNO" -> EnumTipoTurno.CONTRATURNO;
            default -> throw new BadRequestException("Turno inválido: " + parser.getText());
        };

    }
}
