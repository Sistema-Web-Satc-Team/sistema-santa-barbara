package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.model.Papel;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PapelRepository extends Repository<Papel, Integer> {
    Optional<Papel> findByNome(String nome);

    List<Papel> findAllByNomeIn(Set<String> nomes);
}
