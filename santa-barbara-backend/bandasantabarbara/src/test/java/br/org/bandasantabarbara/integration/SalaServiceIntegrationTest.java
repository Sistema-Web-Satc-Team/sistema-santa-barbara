package br.org.bandasantabarbara.integration;


import br.org.bandasantabarbara.application.dtos.sala.SalaRequestDTO;
import br.org.bandasantabarbara.application.dtos.sala.SalaResponseDTO;
import br.org.bandasantabarbara.application.services.turma.SalaService;
import br.org.bandasantabarbara.exception.ConflictException;
import br.org.bandasantabarbara.model.turma.Turma;
import br.org.bandasantabarbara.repositories.turma.SalaRepository;
import br.org.bandasantabarbara.repositories.turma.TurmaRepository;
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

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private TurmaRepository turmaRepository;


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

    @Test
    void deveAtualizarCodigoECapacidadeDaSala() {
        salaService.cadastrar(new SalaRequestDTO("1.10", 10));

        SalaResponseDTO atualizada = salaService.atualizar("1.10", new SalaRequestDTO("2.10", 20));

        assertEquals("2.10", atualizada.code());
        assertEquals(20, atualizada.capacidade());
        assertEquals(atualizada.id(), salaService.pegarPorCodigo("2.10").id());
    }

    @Test
    void naoDeveAtualizarSalaParaCodigoJaExistente() {
        salaService.cadastrar(new SalaRequestDTO("1.10", 10));
        salaService.cadastrar(new SalaRequestDTO("1.11", 15));

        assertThrows(ConflictException.class, () ->
            salaService.atualizar("1.10", new SalaRequestDTO("1.11", 10))
        );
    }

    @Test
    void deveDeletarSalaSemTurmaAssociada() {
        salaService.cadastrar(new SalaRequestDTO("1.10", 10));

        salaService.deletar("1.10");

        Assertions.assertThat(salaRepository.findByCode("1.10")).isEmpty();
    }

    @Test
    void naoDeveDeletarSalaAssociadaATurma() {
        salaService.cadastrar(new SalaRequestDTO("1.10", 10));
        var sala = salaService.buscarEntidadePorCodigo("1.10");
        turmaRepository.save(new Turma("turma de teste", sala));

        assertThrows(ConflictException.class, () -> salaService.deletar("1.10"));
    }

    @Test
    void naoDeveReduzirCapacidadeAbaixoDasVagasDaTurmaAssociada() {
        salaService.cadastrar(new SalaRequestDTO("1.10", 20));
        var sala = salaService.buscarEntidadePorCodigo("1.10");
        Turma turma = new Turma("turma de teste", sala);
        turma.setNumeroVagas(15);
        turmaRepository.save(turma);

        assertThrows(ConflictException.class, () ->
            salaService.atualizar("1.10", new SalaRequestDTO("1.10", 10))
        );
    }

}
