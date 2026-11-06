package br.org.bandasantabarbara.repositories.membro;

import br.org.bandasantabarbara.model.membro.Membro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.*;


public interface MembroRepository extends JpaRepository<Membro, UUID>, JpaSpecificationExecutor<Membro> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM Membro m WHERE m.id = :id")
    Optional<Membro> findByIdForUpdate(@Param("id") UUID id);

    @Query("SELECT m FROM Membro m WHERE m.id IN :ids")
    Collection<Membro> findAllByIds(@Param("ids") Set<UUID> ids);

    @Query("""
        SELECT DISTINCT m
        FROM Membro m
        LEFT JOIN FETCH m.vinculos v
        LEFT JOIN FETCH v.funcao
        WHERE m.id = :id
    """)
    Optional<Membro> findByIdWithVinculos(@Param("id") UUID id);


    @Query("SELECT m FROM Membro m JOIN FETCH m.avatarFile WHERE m.avatarFile.id = :fileId")
    Optional<Membro> findByFileIdWithFile(@Param("fileId") UUID fileId);


    @Query("""
        SELECT DISTINCT m
        FROM Membro m
        LEFT JOIN FETCH m.vinculos v
        LEFT JOIN FETCH v.funcao
        WHERE m.id IN :ids
    """)
    List<Membro> findAllWithVinculosByIds(
        @Param("ids") Collection<UUID> ids
    );

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(
        String email,
        UUID id
    );
}