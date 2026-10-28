package br.org.bandasantabarbara.integration;

import br.org.bandasantabarbara.model.Horario;
import br.org.bandasantabarbara.repositories.HorarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalTime;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class HorarioRepositoryIntegrationTest {

    @Autowired
    private HorarioRepository horarioRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void deveSalvarHorarioComSeusIntervalos() {
        var horarioLocal = new Horario();

        horarioLocal.setSegunda(
                LocalTime.of(19, 0),
                LocalTime.of(20, 0)
        );

        horarioLocal.setSexta(
                LocalTime.of(19, 0),
                LocalTime.of(20, 0)
        );

        horarioLocal.setSexta(
                LocalTime.of(20, 0),
                LocalTime.of(21, 0)
        );

        var horarioSalvo = horarioRepository.save(horarioLocal);

        entityManager.flush();
        entityManager.clear();

        var horarioRecuperado = horarioRepository
                .findById(horarioSalvo.getId())
                .orElseThrow();

        Assertions.assertEquals(
                horarioLocal.getSegunda().get(0).getInicio(),
                horarioRecuperado.getSegunda().get(0).getInicio()
        );

        Assertions.assertEquals(
                horarioLocal.getSegunda().get(0).getFim(),
                horarioRecuperado.getSegunda().get(0).getFim()
        );
    }


}
