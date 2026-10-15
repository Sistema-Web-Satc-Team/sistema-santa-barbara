package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Convite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

public interface ConviteRepository extends JpaRepository<Convite, UUID> {

    @EntityGraph(attributePaths = {"membro"})
    @Override
    Optional<Convite> findById(UUID id);

    @EntityGraph(attributePaths = {"membro"})
    @Query("SELECT c FROM Convite c")
    Page<Convite> listarTodosConvites(Pageable pageable);


    boolean existsById(UUID id);


    Optional<Convite> findFirstByMembroIdOrderByDataCriacaoConviteDesc(
            UUID idMembro
    );

}
