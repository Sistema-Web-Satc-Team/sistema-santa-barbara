package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Funcao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FuncaoRepository extends Repository<Funcao, Integer> {

    Optional<Funcao> findByCode(String code);

    List<Funcao> findByNomeContaining(String nome);

    Optional<Funcao> findById(Integer id);

    List<Funcao> findAll();

    @Query("""
        SELECT f
        FROM Funcao f
        WHERE f.nome ILIKE :term
           OR f.code = :term
    """)
    Optional<Funcao> findFuncaoByCodeOrNome(@Param("term") String term);


    List<Funcao> findByCodeIn(Collection<String> codes);
}
