package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Genero;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface GeneroRepository extends JpaRepository<Genero, Integer> {

    List<Genero> findAllBySlugIn(List<String> slugs);
}
