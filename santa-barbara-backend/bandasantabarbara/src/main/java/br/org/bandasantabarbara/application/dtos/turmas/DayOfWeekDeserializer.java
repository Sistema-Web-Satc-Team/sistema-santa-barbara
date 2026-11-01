package br.org.bandasantabarbara.application.dtos.turmas;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import br.org.bandasantabarbara.exception.BadRequestException;

public class DayOfWeekDeserializer extends JsonDeserializer<DayOfWeek> {

    @Override
    public DayOfWeek deserialize(
        JsonParser parser,
        DeserializationContext context
    ) throws IOException {

        String valor = parser.getText()
            .trim()
            .toUpperCase(Locale.ROOT)
            .replace("-FEIRA", "");

        try {
            return DayOfWeek.valueOf(valor);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                "Dia da semana inválido: " + parser.getText()
            );
        }
    }
}