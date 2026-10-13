package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Partitura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PartituraRepository extends JpaRepository<Partitura, UUID> {
    Page<Partitura> findAllByMusicaId_In(List<UUID> musicaIds, Pageable pageable);

    @Query("SELECT p FROM Partitura p JOIN FETCH p.file WHERE p.file.id = :fileId")
    Optional<Partitura> findByFileIdWithFile(@Param("fileId") UUID fileId);


    List<Partitura> findByMusicaId(UUID musicaId);
}
