package br.org.bandasantabarbara.repositories.turma;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.org.bandasantabarbara.model.turma.Sala;

import java.util.Optional;
import java.util.List;
import jakarta.persistence.LockModeType;


public interface SalaRepository extends JpaRepository<Sala, Integer> {


    Optional<Sala> findByCode(String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Sala s WHERE s.code = :code")
    Optional<Sala> findByCodeForUpdate(@Param("code") String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Sala s WHERE s.id = :id")
    Optional<Sala> findByIdForUpdate(@Param("id") Integer id);

    boolean existsByCodeAndIdNot(String code, Integer id);

    List<Sala> findByCapacidade(Integer capacidade);
    

}
