package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.model.Papel;
import br.org.bandasantabarbara.model.Permissao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PapelRepository extends Repository<Papel, Integer> {

    Papel save(Papel papel);

    @Query("SELECT p FROM Papel p")
    List<Papel> listar(Pageable pageable);

    @Modifying
    @Query("""
        DELETE FROM Papel p
        WHERE p.nome = :nome
          AND NOT EXISTS (
              SELECT 1
              FROM Membro m
              WHERE p MEMBER OF m.papeis
          )
    """)
    int excluirSeNaoPossuirMembros(@Param("nome") String nome);

    Optional<Papel> findByNome(String nome);

    List<Papel> findAllByNomeIn(Set<String> nomes);


    @Query("SELECT p FROM Papel p WHERE p.nome = :nome")
    @EntityGraph(attributePaths = {"permissoes"})
    Optional<Papel> findByNomeWithPermissoes(@Param("nome") String nome);



}
