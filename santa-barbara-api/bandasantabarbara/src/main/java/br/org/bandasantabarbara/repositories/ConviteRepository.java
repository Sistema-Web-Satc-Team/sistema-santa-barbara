package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Convite;
import br.org.bandasantabarbara.model.Membro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConviteRepository extends Repository<Convite, UUID> {

    Convite save(Convite convite);

    @EntityGraph(attributePaths = {"membro"})
    Optional<Convite> findById(UUID id);

    @EntityGraph(attributePaths = {"membro"})
    @Query("SELECT c FROM Convite c")
    List<Convite> listarTodosConvites(Pageable pageable);

    boolean existsByMembroAndDataExpiracaoConviteAfter(Membro membro, Instant dataAtual);
}
