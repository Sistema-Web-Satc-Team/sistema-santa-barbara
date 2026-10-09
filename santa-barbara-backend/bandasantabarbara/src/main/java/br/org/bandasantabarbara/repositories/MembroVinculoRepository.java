package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.model.MembroVinculo;
import br.org.bandasantabarbara.model.MembroVinculoId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembroVinculoRepository extends Repository<MembroVinculo, MembroVinculoId> {

    MembroVinculo save(MembroVinculo membroVinculo);

    Optional<MembroVinculo> findById(MembroVinculoId id);

    Page<MembroVinculo> findAll(Pageable page);

    Page<MembroVinculo> findByMembroId(UUID idMembro, Pageable page);

    Page<MembroVinculo> findByFuncaoId(long idFuncao, Pageable page);

    @EntityGraph(attributePaths = "funcao")
    List<MembroVinculo> findByMembroIdAndDataTerminoIsNullOrderByDataInicioDesc(UUID membroId);

    @EntityGraph(attributePaths = "funcao")
    List<MembroVinculo> findByMembroIdAndDataTerminoIsNotNullOrderByDataInicioDesc(UUID membroId);


    boolean existsByFuncaoCodeAndDataTerminoIsNull(String code);
}