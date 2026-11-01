package br.org.bandasantabarbara.repositories.turma;

import org.springframework.data.jpa.repository.JpaRepository;

import br.org.bandasantabarbara.model.turma.Sala;

import java.util.Optional;
import java.util.List;


public interface SalaRepository extends JpaRepository<Sala, Integer> {


    Optional<Sala> findByCode(String code);

    List<Sala> findByCapacidade(Integer capacidade);
    

}
