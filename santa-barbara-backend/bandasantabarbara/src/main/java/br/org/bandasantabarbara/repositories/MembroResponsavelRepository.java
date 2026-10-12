package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.MembroResponsavel;
import br.org.bandasantabarbara.model.MembroResponsavelId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface MembroResponsavelRepository extends JpaRepository<MembroResponsavel, MembroResponsavelId> {

    List<MembroResponsavel> findByMembroMenorId(UUID idMembroMenor);

    boolean existsByResponsavel_Id(UUID idResponsavel);

    boolean existsByResponsavel_IdAndMembroMenor_Id(UUID idResponsavel, UUID idMembroMenor);

    @EntityGraph(attributePaths = "responsavel")
    List<MembroResponsavel> findByMembroMenor_IdIn(
            Collection<UUID> idsMembroMenor
    );
}
