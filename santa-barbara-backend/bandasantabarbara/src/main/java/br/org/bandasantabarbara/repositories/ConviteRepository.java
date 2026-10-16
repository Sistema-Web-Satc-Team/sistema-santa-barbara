package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Convite;
import br.org.bandasantabarbara.model.EnumConviteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface ConviteRepository extends JpaRepository<Convite, UUID> {

    @EntityGraph(attributePaths = {"membro"})
    @Override
    Optional<Convite> findById(UUID id);

    @EntityGraph(attributePaths = {"membro"})
    @Query("""
            SELECT c FROM Convite c
            WHERE (:search IS NULL OR 
                   LOWER(c.membro.nome) LIKE :search OR 
                   LOWER(c.membro.email) LIKE :search)
              AND (:status IS NULL OR c.status = :status)
    """)
    Page<Convite> listarTodosConvites(@Param("search") String search, @Param("status") EnumConviteStatus status, Pageable pageable);


    boolean existsById(UUID id);


    Optional<Convite> findFirstByMembroIdOrderByDataCriacaoConviteDesc(
            UUID idMembro
    );

}
