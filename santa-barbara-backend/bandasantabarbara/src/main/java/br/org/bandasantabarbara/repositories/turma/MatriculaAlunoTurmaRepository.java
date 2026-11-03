package br.org.bandasantabarbara.repositories.turma;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import br.org.bandasantabarbara.model.turma.MatriculaAlunoTurma;
import br.org.bandasantabarbara.model.turma.ProfessorTurma;
import br.org.bandasantabarbara.repositories.ensino.AlunoInstrumentoProjection;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.LockModeType;

public interface MatriculaAlunoTurmaRepository extends JpaRepository<MatriculaAlunoTurma, Long> {

    @Query("""
        SELECT COUNT(matricula)
        FROM MatriculaAlunoTurma matricula
        WHERE matricula.turma.id = :turmaId
            AND matricula.status = :status
            AND matricula.dataMatricula <= CURRENT_DATE
            AND (matricula.dataSaida IS NULL OR matricula.dataSaida > CURRENT_DATE)
    """)
    long contarMatriculasAtivasPorTurma(
        @Param("turmaId") Long turmaId,
        @Param("status") MatriculaAlunoTurma.EnumStatusMatricula status
    );

    @Query("""
        SELECT
            turma.id AS turmaId,
            turma.nome AS turma,
            membroProfessor.nome AS professor,
            sala.code AS sala,
            turma.turno AS turno
        FROM MatriculaAlunoTurma matricula
        JOIN matricula.turma turma
        JOIN turma.sala sala
        LEFT JOIN turma.professores professorTurma
            ON professorTurma.status = :statusProfessor
        LEFT JOIN professorTurma.professor professor
        LEFT JOIN professor.membro membroProfessor
        WHERE matricula.aluno.id = :alunoId
            AND matricula.status = :statusMatricula
            AND matricula.dataMatricula <= CURRENT_DATE
            AND (matricula.dataSaida IS NULL OR matricula.dataSaida > CURRENT_DATE)
            AND turma.dataInicio IS NOT NULL
            AND turma.dataInicio <= CURRENT_DATE
            AND (turma.dataFim IS NULL OR turma.dataFim > CURRENT_DATE)
            AND turma.dataTermino IS NULL
        ORDER BY turma.nome
    """)
    List<AlunoTurmaProjection> listarTurmasAtuaisDoAluno(
        @Param("alunoId") UUID alunoId,
        @Param("statusMatricula") MatriculaAlunoTurma.EnumStatusMatricula statusMatricula,
        @Param("statusProfessor") ProfessorTurma.EnumStatusTurma statusProfessor
    );

    @Query("""
        SELECT DISTINCT
            matricula.turma.id AS turmaId,
            membro.nome AS colega
        FROM MatriculaAlunoTurma matricula
        JOIN matricula.aluno aluno
        JOIN aluno.membro membro
        WHERE matricula.turma.id IN :turmaIds
            AND matricula.aluno.id <> :alunoId
            AND matricula.status = :status
            AND matricula.dataMatricula <= CURRENT_DATE
            AND (matricula.dataSaida IS NULL OR matricula.dataSaida > CURRENT_DATE)
        ORDER BY matricula.turma.id, membro.nome
    """)
    List<AlunoTurmaColegaProjection> listarColegasAtivosPorTurmas(
        @Param("turmaIds") Collection<Long> turmaIds,
        @Param("alunoId") UUID alunoId,
        @Param("status") MatriculaAlunoTurma.EnumStatusMatricula status
    );

    @Query("""
        SELECT DISTINCT
            instrumentoTurma.turma.id AS turmaId,
            instrumento.nome AS instrumento
        FROM InstrumentoTurma instrumentoTurma
        JOIN instrumentoTurma.instrumento instrumento
        WHERE instrumentoTurma.turma.id IN :turmaIds
        ORDER BY instrumentoTurma.turma.id, instrumento.nome
    """)
    List<AlunoTurmaInstrumentoProjection> listarInstrumentosPorTurmas(
        @Param("turmaIds") Collection<Long> turmaIds
    );

    @Query("""
        SELECT
            turma.id AS turmaId,
            intervalo.diaSemana AS diaDaSemana,
            intervalo.inicio AS inicio,
            intervalo.fim AS fim
        FROM MatriculaAlunoTurma matricula
        JOIN matricula.turma turma
        JOIN turma.horario horario
        JOIN horario.intervalos intervalo
        WHERE matricula.aluno.id = :alunoId
            AND matricula.status = :status
            AND matricula.dataMatricula <= CURRENT_DATE
            AND (matricula.dataSaida IS NULL OR matricula.dataSaida > CURRENT_DATE)
            AND turma.dataInicio IS NOT NULL
            AND turma.dataInicio <= CURRENT_DATE
            AND (turma.dataFim IS NULL OR turma.dataFim > CURRENT_DATE)
            AND turma.dataTermino IS NULL
        ORDER BY turma.id, intervalo.diaSemana, intervalo.inicio
    """)
    List<AlunoTurmaHorarioProjection> listarHorariosAtuaisDoAluno(
        @Param("alunoId") UUID alunoId,
        @Param("status") MatriculaAlunoTurma.EnumStatusMatricula status
    );

    @Query("""
        SELECT 
            m.turma.id AS idTurma,
            COUNT(m) AS quantidade
        FROM MatriculaAlunoTurma m
        WHERE m.turma.id IN :idsTurmas
        AND m.status = 'ATIVO'
        GROUP BY m.turma.id
    """)
    List<ContagemPorTurma> contarMatriculasAtivasPorTurmas(
        Collection<Long> idsTurmas
    );

    @EntityGraph(attributePaths = {"turma", "aluno.membro"})
    @Query("""
        SELECT m FROM MatriculaAlunoTurma m
        WHERE :status IS NULL OR m.status = :status
    """)
    Page<MatriculaAlunoTurma> listarPorStatus(
        @Param("status") MatriculaAlunoTurma.EnumStatusMatricula status,
        Pageable pageable
    );

    @EntityGraph(attributePaths = {"turma", "aluno.membro"})
    @Query("""
        SELECT m FROM MatriculaAlunoTurma m
        WHERE m.aluno.id = :alunoId
            AND (:status IS NULL OR m.status = :status)
    """)
    Page<MatriculaAlunoTurma> listarPorAlunoEStatus(
        @Param("alunoId") UUID alunoId,
        @Param("status") MatriculaAlunoTurma.EnumStatusMatricula status,
        Pageable pageable
    );

    @EntityGraph(attributePaths = {"turma", "aluno.membro"})
    @Query("SELECT m FROM MatriculaAlunoTurma m WHERE m.id = :id")
    Optional<MatriculaAlunoTurma> buscarDetalhePorId(@Param("id") Long id);

    boolean existsByTurma_IdAndAluno_IdAndStatus(
        Long turmaId,
        UUID alunoId,
        MatriculaAlunoTurma.EnumStatusMatricula status
    );

    long countByTurma_IdAndStatus(Long turmaId, MatriculaAlunoTurma.EnumStatusMatricula status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM MatriculaAlunoTurma m WHERE m.id = :id")
    Optional<MatriculaAlunoTurma> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM MatriculaAlunoTurma m WHERE m.id = :id AND m.status = 'ATIVO'")
    Optional<MatriculaAlunoTurma> findActiveByIdForUpdate(@Param("id") Long id);

    @Modifying
    @Query(
        value = """
            UPDATE matricula_aluno_turma
            SET status = 'ENCERRADO', data_saida = :dataSaida
            WHERE id_turma = :turmaId AND status = 'ATIVO'
            """,
        nativeQuery = true
    )
    int encerrarMatriculasAtivas(@Param("turmaId") Long turmaId, @Param("dataSaida") LocalDate dataSaida);

    @Modifying
    @Query(
        value = """
            UPDATE matricula_aluno_turma mat
            SET status = 'ENCERRADO', data_saida = t.data_fim
            FROM turma t
            WHERE mat.id_turma = t.id
                AND mat.status = 'ATIVO'
                AND t.data_termino IS NULL
                AND t.data_fim <= CURRENT_DATE
            """,
        nativeQuery = true
    )
    int encerrarMatriculasDeTurmasVencidas();

    @Query(
        value = """
            SELECT EXISTS (
                SELECT 1
                FROM matricula_aluno_turma mat
                JOIN turma outra_turma ON outra_turma.id = mat.id_turma
                JOIN horario_intervalo outro_horario
                    ON outro_horario.id_horario = outra_turma.horario_id
                JOIN horario_intervalo novo_horario
                    ON novo_horario.id_horario = :horarioId
                WHERE mat.id_aluno = :alunoId
                    AND mat.status = 'ATIVO'
                    AND mat.id_turma <> :turmaId
                    AND (:turmaOrigemId IS NULL OR mat.id_turma <> :turmaOrigemId)
                    AND outra_turma.data_inicio <= :dataFim
                    AND (
                        COALESCE(outra_turma.data_termino, outra_turma.data_fim) IS NULL
                        OR COALESCE(outra_turma.data_termino, outra_turma.data_fim) >= :dataInicio
                    )
                    AND outro_horario.dia_semana = novo_horario.dia_semana
                    AND outro_horario.inicio < novo_horario.fim
                    AND outro_horario.fim > novo_horario.inicio
            )
            """,
        nativeQuery = true
    )
    boolean existsScheduleConflict(
        @Param("alunoId") UUID alunoId,
        @Param("turmaId") Long turmaId,
        @Param("turmaOrigemId") Long turmaOrigemId,
        @Param("horarioId") Long horarioId,
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim
    );

    @Query(
        value = """
            SELECT EXISTS (
                SELECT 1
                FROM matricula_aluno_turma matricula_atual
                JOIN horario_intervalo horario_atual
                    ON horario_atual.id_horario = :horarioId
                JOIN matricula_aluno_turma matricula_outra
                    ON matricula_outra.id_aluno = matricula_atual.id_aluno
                    AND matricula_outra.id_turma <> :turmaId
                    AND matricula_outra.status = 'ATIVO'
                JOIN turma outra_turma ON outra_turma.id = matricula_outra.id_turma
                JOIN horario_intervalo outro_horario
                    ON outro_horario.id_horario = outra_turma.horario_id
                WHERE matricula_atual.id_turma = :turmaId
                    AND matricula_atual.status = 'ATIVO'
                    AND outra_turma.data_inicio <= :dataFim
                    AND (
                        COALESCE(outra_turma.data_termino, outra_turma.data_fim) IS NULL
                        OR COALESCE(outra_turma.data_termino, outra_turma.data_fim) >= :dataInicio
                    )
                    AND outro_horario.dia_semana = horario_atual.dia_semana
                    AND outro_horario.inicio < horario_atual.fim
                    AND outro_horario.fim > horario_atual.inicio
            )
            """,
        nativeQuery = true
    )
    boolean existsScheduleConflictForTurma(
        @Param("turmaId") Long turmaId,
        @Param("horarioId") Long horarioId,
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim
    );

    @Query(
            value = """
                    SELECT DISTINCT mat.id_aluno
                    FROM matricula_aluno_turma mat
                    WHERE mat.id_aluno IN (:alunoIds)
                        AND mat.status = 'ATIVO'
                        AND mat.data_matricula <= CURRENT_DATE
                        AND (
                            mat.data_saida IS NULL
                            OR mat.data_saida > CURRENT_DATE
                        )
                    """,
            nativeQuery = true
    )
    List<UUID> findAlunoIdsComMatriculaAtiva(@Param("alunoIds") Collection<UUID> alunoIds);

    @Query(
            value = """
                    SELECT DISTINCT
                        mat.id_aluno AS "alunoId",
                        i.nome AS instrumento
                    FROM matricula_aluno_turma mat
                    JOIN instrumento_turma it ON it.id_turma = mat.id_turma
                    JOIN instrumento i ON i.id = it.id_instrumento
                    WHERE mat.id_aluno IN (:alunoIds)
                        AND mat.status = 'ATIVO'
                        AND mat.data_matricula <= CURRENT_DATE
                        AND (
                            mat.data_saida IS NULL
                            OR mat.data_saida > CURRENT_DATE
                        )
                    """,
            nativeQuery = true
    )
    List<AlunoInstrumentoProjection> findInstrumentosDeAlunosComMatriculaAtiva(
            @Param("alunoIds") Collection<UUID> alunoIds
    );


    // Achar a as matrículas ativas de uma turma específica

    @Query("""
        SELECT m.id
        FROM MatriculaAlunoTurma m
        WHERE m.turma.id = :turmaId
            AND m.status = 'ATIVO'
            AND m.dataMatricula <= CURRENT_DATE
            AND (m.dataSaida IS NULL OR m.dataSaida > CURRENT_DATE)
    """)
    Set<Long> findAllIdsAtivasByTurmaId(@Param("turmaId") Long turmaId);


}
