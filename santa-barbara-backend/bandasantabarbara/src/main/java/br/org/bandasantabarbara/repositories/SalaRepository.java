package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalaRepository extends JpaRepository<Sala, Integer> {


    Optional<Sala> findByCode(String code);

}
