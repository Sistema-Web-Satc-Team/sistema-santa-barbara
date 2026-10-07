package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Funcao;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface FuncaoRepository extends Repository<Funcao, Long> {

    Funcao save(Funcao funcao);

    Optional<Funcao> findByCode(String code);

    List<Funcao> findByNomeContaining(String nome);

    Optional<Funcao> findById(long id);

    List<Funcao> findAll();
}
