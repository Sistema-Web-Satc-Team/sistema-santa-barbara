package br.org.bandasantabarbara.unit;

import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.model.Sala;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ActiveProfiles("test")
public class SalaTest {


    @Test
    public void DeveCriarUmaSalaComCapacidadeECodigo() {
        var sala = new Sala("1.10", 15);

        assertEquals( 15, sala.getCapacidade());
        assertEquals( "1.10", sala.getCode());
    }


    @Test
    public void DeveAtualizarOsDadosDaSala() {
        var sala = new Sala("1.10", 15);

        sala.setCode("1.12");
        sala.setCapacidade(20);

        assertEquals(20, sala.getCapacidade());
        assertEquals("1.12", sala.getCode());
    }


    @Test
    public void DeveImpedirCapacidadeOuCodigoNulo() {
        var sala = new Sala("1.10", 15);

        assertThrows(BadRequestException.class, () -> sala.setCode(null));
        assertThrows(BadRequestException.class, () -> sala.setCode(""));
        assertThrows(BadRequestException.class, () -> sala.setCapacidade(null));
        assertThrows(BadRequestException.class, () -> sala.setCapacidade(0));
    }
}
