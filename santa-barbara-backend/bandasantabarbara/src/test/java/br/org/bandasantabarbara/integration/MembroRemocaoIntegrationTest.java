package br.org.bandasantabarbara.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.org.bandasantabarbara.application.services.membro.MembroService;
import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.membro.Funcao;
import br.org.bandasantabarbara.model.membro.Membro;
import br.org.bandasantabarbara.model.membro.MembroCredencial;
import br.org.bandasantabarbara.model.membro.MembroVinculo;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MembroRemocaoIntegrationTest {

    @Autowired
    private MembroService membroService;

    @Autowired
    private EntityManager entityManager;

    @Test
    void removeMembroSemVinculosOuCredencial() {
        Membro membro = criarMembro();
        UUID membroId = membro.getId();

        membroService.remover(membroId);

        entityManager.flush();
        assertThat(entityManager.find(Membro.class, membroId)).isNull();
    }

    @Test
    void impedeRemocaoDeMembroComVinculo() {
        Membro membro = criarMembro();
        Funcao funcao = new Funcao("TESTE_REMOCAO", "Teste remoção", Funcao.TipoFuncaoEnum.DOMINIO);
        entityManager.persist(funcao);
        entityManager.persist(new MembroVinculo(membro, funcao, LocalDate.now()));
        entityManager.flush();

        assertThatThrownBy(() -> membroService.remover(membro.getId()))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Não é possível remover um membro que possui vínculos.");
        assertThat(entityManager.find(Membro.class, membro.getId())).isNotNull();
    }

    @Test
    void impedeRemocaoDeMembroComVinculoEncerrado() {
        Membro membro = criarMembro();
        Funcao funcao = new Funcao("TESTE_REMOCAO_ENCERRADO", "Teste remoção encerrado", Funcao.TipoFuncaoEnum.DOMINIO);
        entityManager.persist(funcao);
        MembroVinculo vinculo = new MembroVinculo(membro, funcao, LocalDate.now().minusDays(1));
        vinculo.encerrar();
        entityManager.persist(vinculo);
        entityManager.flush();

        assertThatThrownBy(() -> membroService.remover(membro.getId()))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Não é possível remover um membro que possui vínculos.");
        assertThat(entityManager.find(Membro.class, membro.getId())).isNotNull();
    }

    @Test
    void impedeRemocaoDeMembroComCredencial() {
        Membro membro = criarMembro();
        MembroCredencial credencial = new MembroCredencial(membro);
        credencial.setHashSenha("hash-teste");
        entityManager.persist(credencial);
        entityManager.flush();

        assertThatThrownBy(() -> membroService.remover(membro.getId()))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Não é possível remover um membro que possui credencial associada.");
        assertThat(entityManager.find(Membro.class, membro.getId())).isNotNull();
    }

    @Test
    void membroInexistenteRetornaNaoEncontrado() {
        assertThatThrownBy(() -> membroService.remover(UUID.randomUUID()))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Membro não encontrado.");
    }

    private Membro criarMembro() {
        Membro membro = new Membro();
        membro.setNome("membro para remoção");
        membro.setDataNascimento(LocalDate.now().minusYears(20));
        membro.setSexo(Membro.MembroSexoEnum.NAO_INFORMADO);
        membro.setEmail(UUID.randomUUID() + "@example.test");
        membro.setTelefone("48000000001");
        entityManager.persist(membro);
        entityManager.flush();
        return membro;
    }
}
