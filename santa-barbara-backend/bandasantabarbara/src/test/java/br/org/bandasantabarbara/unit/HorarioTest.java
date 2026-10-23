package br.org.bandasantabarbara.unit;

import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.model.Horario;
import br.org.bandasantabarbara.model.HorarioIntervalo;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/* Tabela de horários:

Esse é um horário primitivo, sem assuntos ou eventos que ocorreram entre os horários.

 -----------------------------------------------------
 |    Horário    |     Segunda     |      Terça      |
 -----------------------------------------------------
 |       -       |  10:00 - 11:00  |  19:00 - 20:00  |
 |       -       |  11:00 - 12:00  |  20:00 - 21:00  |
 |       -       |       ...       |       ...       |
 -----------------------------------------------------

*/


public class HorarioTest {


    @Test
    public void DeveCriarUmHorario() {
        Horario horario = new Horario();

        horario.setSegunda(LocalTime.of(10,0),LocalTime.of(11,0));
        horario.setSegunda(LocalTime.of(11,0),LocalTime.of(12,0));
        horario.setSexta(LocalTime.of(11,0),LocalTime.of(12,0));

        List<HorarioIntervalo> segundaFeira = horario.getSegunda();
        List<HorarioIntervalo> sextaFeira = horario.getSexta();

        assertEquals(2, segundaFeira.size());
        assertEquals(LocalTime.of(10, 0), segundaFeira.get(0).getInicio());
        assertEquals(LocalTime.of(12, 0), segundaFeira.get(1).getFim());
        assertEquals(LocalTime.of(12, 0), sextaFeira.get(0).getFim());
    }

    @Test
    public void NaoPodeColidirHorarios() {
        Horario horario = new Horario();

        horario.setSegunda(LocalTime.of(10,0),LocalTime.of(11,0));


        assertThrows(BadRequestException.class, () -> {
            horario.setSegunda(LocalTime.of(10,0),LocalTime.of(11,0));
        });

        assertThrows(BadRequestException.class, () -> {
            horario.setSegunda(LocalTime.of(10,30),LocalTime.of(10,45));
        });


    }


}
