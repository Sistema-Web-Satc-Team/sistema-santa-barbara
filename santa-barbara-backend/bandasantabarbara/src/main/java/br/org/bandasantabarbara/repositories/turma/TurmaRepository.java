package br.org.bandasantabarbara.repositories.turma;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.org.bandasantabarbara.model.turma.StatusTurma;
import br.org.bandasantabarbara.model.turma.Turma;
import java.time.LocalDate;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;


public interface TurmaRepository extends JpaRepository<Turma, Long> {


    @Query(
        value = """
            SELECT t FROM Turma t
            JOIN FETCH t.sala
            LEFT JOIN FETCH t.horario
            WHERE :status IS NULL OR t.status = :status
        """,
        countQuery = """
            SELECT COUNT(t) FROM Turma t
            WHERE :status IS NULL OR t.status = :status
        """
    )
    Page<Turma> findAllWithHorarioSalaAndOptionalFilters(
            @Param("status") StatusTurma status,
            Pageable pageable
    );


    @Query(
        value = """
            SELECT t FROM Turma t
            JOIN FETCH t.sala
            LEFT JOIN FETCH t.instrumentos
            WHERE :status IS NULL OR t.status = :status
        """,
        countQuery = """
            SELECT COUNT(t) FROM Turma t
            WHERE :status IS NULL OR t.status = :status
        """
    )
    Page<Turma> findAllWithSalaAndOptionalFilters(
            @Param("status") StatusTurma status,
            Pageable pageable
    );



    // @EntityGraph(attributePaths = {"sala", "horario", "horario.intervalos", "instrumentos.instrumento"})
    @Query("""
        SELECT t FROM Turma t
            JOIN FETCH t.sala
            LEFT JOIN FETCH t.horario
        WHERE t.id = :id
    """)
    Optional<Turma> findDetailsById(@Param("id") Long id);

    @Query("""
        SELECT DISTINCT t FROM Turma t
            LEFT JOIN FETCH t.professores pt
            LEFT JOIN FETCH pt.professor p
            LEFT JOIN FETCH p.membro
        WHERE t.id = :id
    """)
    Optional<Turma> findWithProfessoresById(@Param("id") Long id);

    @Query("""
        SELECT DISTINCT t FROM Turma t
            LEFT JOIN FETCH t.matriculas mt
            LEFT JOIN FETCH mt.aluno a
            LEFT JOIN FETCH a.membro
        WHERE t.id = :id
    """)
    Optional<Turma> findWithMatriculasById(@Param("id") Long id);

    @Query("""
        SELECT DISTINCT t FROM Turma t
            LEFT JOIN FETCH t.instrumentos it
            LEFT JOIN FETCH it.instrumento
        WHERE t.id = :id
    """)
    Optional<Turma> findWithInstrumentosById(@Param("id") Long id);

    @Query(
        value = """
            SELECT EXISTS (
                SELECT 1
                FROM turma t
                WHERE t.sala_id = :salaId
                    AND (:turmaId IS NULL OR t.id <> :turmaId)
                    AND t.data_inicio <= :dataFim
                    AND (
                        COALESCE(t.data_termino, t.data_fim) IS NULL
                        OR COALESCE(t.data_termino, t.data_fim) >= :dataInicio
                    )
            )
            """,
        nativeQuery = true
    )
    boolean existsRoomConflict(
        @Param("salaId") Integer salaId,
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim,
        @Param("turmaId") Long turmaId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT t FROM Turma t
            WHERE t.id = :id
            """)
    Optional<Turma> findByIdForUpdate(@Param("id") Long id);

    boolean existsBySala_Id(Integer salaId);

    boolean existsBySala_IdAndNumeroVagasGreaterThan(Integer salaId, Integer capacidade);

    @Modifying
    @Query(
        value = """
            UPDATE turma
            SET data_termino = data_fim
            WHERE data_termino IS NULL
                AND data_fim <= CURRENT_DATE
            """,
        nativeQuery = true
    )
    int encerrarTurmasVencidas();
}
