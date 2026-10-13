package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Musica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface MusicaRepository extends JpaRepository<Musica, UUID> {

    @Query("""
        SELECT m FROM Musica m
        WHERE :term IS NULL OR :term = '' 
        OR lower(m.titulo) LIKE concat('%', lower(:term), '%') ESCAPE '\\'
        OR lower(m.compositor) LIKE concat('%', lower(:term), '%') ESCAPE '\\'
    """)
    Page<Musica> findAll(@Param("term") String term, Pageable pageable);

}
