package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Permissao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Set;

public interface PermissaoRepository extends Repository<Permissao, Integer> {

    Set<Permissao> findByNomeIn(Set<String> nomes);

    @Query("SELECT p FROM Permissao p")
    List<Permissao> listar(Pageable pageable);
}
