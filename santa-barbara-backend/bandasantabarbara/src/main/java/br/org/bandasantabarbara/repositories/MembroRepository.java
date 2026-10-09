package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Membro;
import jakarta.persistence.Entity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface MembroRepository extends JpaRepository<Membro, UUID>, JpaSpecificationExecutor<Membro> {

    @Query("SELECT m FROM Membro m WHERE m.id IN :ids")
    Collection<Membro> findAllByIds(@Param("ids") Set<UUID> ids);

}