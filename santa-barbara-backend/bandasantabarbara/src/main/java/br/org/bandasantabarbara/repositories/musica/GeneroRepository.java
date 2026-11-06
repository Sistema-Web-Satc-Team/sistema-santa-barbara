package br.org.bandasantabarbara.repositories.musica;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.org.bandasantabarbara.model.musica.Genero;

import java.util.List;
import java.util.Set;

public interface GeneroRepository extends JpaRepository<Genero, Integer> {

    List<Genero> findAllBySlugIn(List<String> slugs);


    @Query(value = """
        select * from genero g
        where unaccent(lower(g.nome)) like unaccent(lower(:termo)) || '%'
        order by g.nome asc
    """, nativeQuery = true)
    List<Genero> buscarPorTermo(@Param("termo") String termo, Pageable pageable);
}
