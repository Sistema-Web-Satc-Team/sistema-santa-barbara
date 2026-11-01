package br.org.bandasantabarbara.repositories.turma;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import br.org.bandasantabarbara.model.turma.Horario;

import java.util.Optional;

public interface HorarioRepository extends JpaRepository<Horario, Long> {

    @EntityGraph(attributePaths = {"intervalos"})
    Optional<Horario> findById(Long id);
}
