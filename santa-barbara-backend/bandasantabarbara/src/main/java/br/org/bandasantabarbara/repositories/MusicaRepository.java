package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Musica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MusicaRepository extends JpaRepository<Musica, UUID> {
}
