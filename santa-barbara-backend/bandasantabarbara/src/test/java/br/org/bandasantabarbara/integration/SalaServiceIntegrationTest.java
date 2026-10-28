package br.org.bandasantabarbara.integration;


import br.org.bandasantabarbara.application.dtos.sala.SalaRequestDTO;
import br.org.bandasantabarbara.application.dtos.sala.SalaResponseDTO;
import br.org.bandasantabarbara.application.services.sala.SalaService;
import br.org.bandasantabarbara.exception.ConflictException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.assertj.core.api.Assertions;

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

    @Test 
    void deveListarAsSalas() {
       salaService.cadastrar(new SalaRequestDTO("1.10", 10));

        List<SalaResponseDTO> salas = salaService.listar(null);

        Assertions.assertThat(salas.size() > 0);
    }

    @Test
    void devePesquisarPorCodigoOuCapacidadeDaSala() {
        salaService.cadastrar(new SalaRequestDTO("1.10", 10));
        salaService.cadastrar(new SalaRequestDTO("1.11", 15));

        SalaResponseDTO pesquisaPorCodigo = salaService.pegarPorCodigo("1.10");
        List<SalaResponseDTO> pesquisaPorCapacidade = salaService.listar(10);

        Assertions.assertThat(pesquisaPorCodigo.code().equals("1.10"));

        Assertions.assertThat(pesquisaPorCapacidade.size() == 1);
    }


    /*
    * @Test
    * void naoDeveRemoverUmaSalaEmCasoDeExistirUmaTurmaVinculadaEmAndamentoOuEmPlanejamento
    */


    /*
    * @Test
    * void devePermitirSoftDeleteEmCasoDeNaoExistirMaisNenhumaTurmaVinculada
    */
}
