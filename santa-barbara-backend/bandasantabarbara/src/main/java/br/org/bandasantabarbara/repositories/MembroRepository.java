package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Membro;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.util.*;

public interface MembroRepository extends Repository<Membro, UUID> {

    Membro save(Membro membro);

    Optional<Membro> findById(UUID id);

    @Query("SELECT m FROM Membro m WHERE m.id IN :ids")
    Collection<Membro> findAllByIds(@Param("ids") Set<UUID> ids);

    Page<Membro> findAll(Pageable page);

    @Query("""
        SELECT m
        FROM Membro m
        WHERE m.nome LIKE %:nome%
           OR m.email LIKE %:email%
    """)
    Page<Membro> findByNomeOrEmail(
            @Param("nome") String nome,
            @Param("email") String email,
            Pageable page
    );

}
