package br.org.bandasantabarbara.integration;


import br.org.bandasantabarbara.application.dtos.sala.SalaRequestDTO;
import br.org.bandasantabarbara.application.dtos.sala.SalaResponseDTO;
import br.org.bandasantabarbara.application.services.sala.SalaService;
import br.org.bandasantabarbara.exception.ConflictException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SalaServiceIntegrationTest {

    @Autowired
    private SalaService salaService;


    @Test
    void deveRegistrarUmaNovaSala() {
        SalaResponseDTO dto =salaService.cadastrar(new SalaRequestDTO("1.10", 10));

        assertEquals("1.10", dto.code());
        assertEquals(10, dto.capacidade());
    }

    @Test
    void naoDeveRegistarUmaSalaComMesmoCodigo() {

        salaService.cadastrar(new SalaRequestDTO("1.10", 10));

        assertThrows(ConflictException.class, () -> {
            salaService.cadastrar(new SalaRequestDTO("1.10", 10));
        });
    }


}
