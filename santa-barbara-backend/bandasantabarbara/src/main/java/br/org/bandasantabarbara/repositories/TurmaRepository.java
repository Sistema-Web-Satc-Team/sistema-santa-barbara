package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Turma;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface TurmaRepository extends JpaRepository<Turma, Long> {


    @Query("""
        SELECT t FROM Turma t
        JOIN FETCH t.sala
        JOIN FETCH t.horario
        WHERE :status IS NULL OR t.status = :status
    """)
    Page<Turma> findAllWithHorarioSalaAndOptionalFilters(
            @Param("status") StatusTurma status,
            Pageable pageable
    );
}
