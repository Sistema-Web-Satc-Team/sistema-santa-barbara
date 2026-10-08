package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.model.MembroCredencial;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface MembroCredencialRepository extends Repository<MembroCredencial, UUID> {
    MembroCredencial save(MembroCredencial membroCredencial);

    @EntityGraph(attributePaths = "membro")
    @Query("SELECT c FROM MembroCredencial c WHERE c.idMembro = :id")
    Optional<MembroCredencial> findByIdWithMembro(@Param("id") UUID id);


    @EntityGraph(attributePaths = "membro")
    @Query("""
        SELECT c
        FROM MembroCredencial c
        WHERE c.nomeUsuario = :login
           OR c.membro.email = :login
    """)
    Optional<MembroCredencial> findByLogin(@Param("login") String login);



}
