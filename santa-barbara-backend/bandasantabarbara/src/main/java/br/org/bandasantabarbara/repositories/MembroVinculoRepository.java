package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.MembroVinculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembroVinculoRepository extends Repository<MembroVinculo, Long> {

    MembroVinculo save(MembroVinculo membroVinculo);

    Optional<MembroVinculo> findById(long id);

    Page<MembroVinculo> findAll(Pageable page);

    Page<MembroVinculo> findByMembroId(UUID idMembro, Pageable page);

    Page<MembroVinculo> findByFuncaoId(long idFuncao, Pageable page);

    @EntityGraph(attributePaths = "funcao")
    List<MembroVinculo> findByMembroIdAndDataTerminoIsNullOrderByDataInicioDesc(
            UUID idMembro
    );


    boolean existsByFuncaoCodeAndDataTerminoIsNull(String code);
}