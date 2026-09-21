package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Convite;
import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.UUID;

public interface ConviteRepository extends Repository<Convite, UUID> {

    Convite save(Convite convite);
    Optional<Convite> findById(UUID id);
}
