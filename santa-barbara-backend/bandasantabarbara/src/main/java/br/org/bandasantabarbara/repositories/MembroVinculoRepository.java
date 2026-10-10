package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.MembroVinculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembroVinculoRepository extends Repository<MembroVinculo, Integer> {

    MembroVinculo save(MembroVinculo membroVinculo);

    @Query("select v from MembroVinculo v where v.id = :idVinculo and v.membro.id = :idMembro")
    Optional<MembroVinculo> findByIdAndMembroId(@Param("idVinculo") Integer idVinculo,
                                                @Param("idMembro") UUID idMembro);

    @EntityGraph(attributePaths = "funcao")
    List<MembroVinculo> findByMembroId(UUID idMembro);

    boolean existsByFuncaoCodeAndDataTerminoIsNull(String code);
}