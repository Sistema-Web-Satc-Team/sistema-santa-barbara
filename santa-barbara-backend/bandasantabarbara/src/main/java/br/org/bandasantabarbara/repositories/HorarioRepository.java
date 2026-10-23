package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Horario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HorarioRepository extends JpaRepository<Horario, Long> {

    @EntityGraph(attributePaths = {"intervalos"})
    Optional<Horario> findById(Long id);
}
