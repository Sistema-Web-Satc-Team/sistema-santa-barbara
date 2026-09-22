package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Convite;
import br.org.bandasantabarbara.model.Membro;
import org.springframework.data.repository.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConviteRepository extends Repository<Convite, UUID> {

    Convite save(Convite convite);
    Optional<Convite> findById(UUID id);

    List<Convite> findByMembro(Membro membro);

    boolean existsByMembroAndDataExpiracaoConviteAfter(Membro membro, Instant dataAtual);
}
