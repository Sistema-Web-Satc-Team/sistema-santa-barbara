package br.org.bandasantabarbara.repositories.turma;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

import br.org.bandasantabarbara.model.turma.ProfessorTurma;
import br.org.bandasantabarbara.model.turma.StatusTurma;
import br.org.bandasantabarbara.model.turma.MatriculaAlunoTurma;
import br.org.bandasantabarbara.repositories.ensino.ProfessorInstrumentoProjection;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface ProfessorTurmaRepository extends JpaRepository<ProfessorTurma, Long> {

    @Query("""
        SELECT
            turma.id AS turmaId,
            turma.nome AS turma,
            membroProfessor.nome AS professor,
            sala.code AS sala,
            turma.turno AS turno
        FROM ProfessorTurma vinculo
        JOIN vinculo.turma turma
        JOIN turma.sala sala
        JOIN vinculo.professor professor
        JOIN professor.membro membroProfessor
        WHERE professor.id = :professorId
            AND vinculo.status = :statusProfessor
            AND vinculo.dataEntrada <= CURRENT_DATE
            AND (vinculo.dataSaida IS NULL OR vinculo.dataSaida > CURRENT_DATE)
            AND turma.dataInicio IS NOT NULL
            AND turma.dataInicio <= CURRENT_DATE
            AND (turma.dataFim IS NULL OR turma.dataFim > CURRENT_DATE)
            AND turma.dataTermino IS NULL
        ORDER BY turma.nome
    """)
    List<AlunoTurmaProjection> listarTurmasAtuaisDoProfessor(
        @Param("professorId") UUID professorId,
        @Param("statusProfessor") ProfessorTurma.EnumStatusTurma statusProfessor
    );

    @Query("""
        SELECT DISTINCT
            matricula.turma.id AS turmaId,
            membroAluno.nome AS aluno
        FROM MatriculaAlunoTurma matricula
        JOIN matricula.aluno aluno
        JOIN aluno.membro membroAluno
        WHERE matricula.turma.id IN :turmaIds
            AND matricula.status = :status
            AND matricula.dataMatricula <= CURRENT_DATE
            AND (matricula.dataSaida IS NULL OR matricula.dataSaida > CURRENT_DATE)
        ORDER BY matricula.turma.id, membroAluno.nome
    """)
    List<ProfessorTurmaAlunoProjection> listarAlunosAtivosPorTurmas(
        @Param("turmaIds") Collection<Long> turmaIds,
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
        FROM ProfessorTurma vinculo
        JOIN vinculo.turma turma
        JOIN turma.horario horario
        JOIN horario.intervalos intervalo
        WHERE vinculo.professor.id = :professorId
            AND vinculo.status = :status
            AND vinculo.dataEntrada <= CURRENT_DATE
            AND (vinculo.dataSaida IS NULL OR vinculo.dataSaida > CURRENT_DATE)
            AND turma.dataInicio IS NOT NULL
            AND turma.dataInicio <= CURRENT_DATE
            AND (turma.dataFim IS NULL OR turma.dataFim > CURRENT_DATE)
            AND turma.dataTermino IS NULL
        ORDER BY turma.id, intervalo.diaSemana, intervalo.inicio
    """)
    List<AlunoTurmaHorarioProjection> listarHorariosAtuaisDoProfessor(
        @Param("professorId") UUID professorId,
        @Param("status") ProfessorTurma.EnumStatusTurma status
    );

    boolean existsByTurma_IdAndProfessor_IdAndStatus(
        Long turmaId,
        UUID professorId,
        ProfessorTurma.EnumStatusTurma status
    );

    long countByTurma_IdAndStatus(Long turmaId, ProfessorTurma.EnumStatusTurma status);

    Optional<ProfessorTurma> findFirstByTurma_IdAndStatus(Long turmaId, ProfessorTurma.EnumStatusTurma status);

    Optional<ProfessorTurma> findFirstByTurma_IdAndProfessor_IdAndStatus(
        Long turmaId,
        UUID professorId,
        ProfessorTurma.EnumStatusTurma status
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT pt FROM ProfessorTurma pt
        WHERE pt.turma.id = :turmaId
            AND pt.professor.id = :professorId
            AND pt.status = 'ATIVO'
    """)
    Optional<ProfessorTurma> findActiveByTurmaAndProfessorForUpdate(
        @Param("turmaId") Long turmaId,
        @Param("professorId") UUID professorId
    );

    @Query("""
        SELECT
            p.turma.id AS idTurma,
            COUNT(p) AS quantidade
        FROM ProfessorTurma p
        WHERE p.turma.id IN :idsTurmas
            AND p.status = 'ATIVO'
        GROUP BY p.turma.id
    """)
    List<ContagemPorTurma> contarProfessoresAtivosPorTurmas(Collection<Long> idsTurmas);

    @Query(
        value = """
            SELECT EXISTS (
                SELECT 1
                FROM professor_turma pt
                JOIN turma outra_turma ON outra_turma.id = pt.id_turma
                JOIN horario_intervalo outro_horario
                    ON outro_horario.id_horario = outra_turma.horario_id
                JOIN horario_intervalo novo_horario
                    ON novo_horario.id_horario = :horarioId
                WHERE pt.id_professor = :professorId
                    AND pt.status = 'ATIVO'
                    AND pt.id_turma <> :turmaId
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
        @Param("professorId") UUID professorId,
        @Param("turmaId") Long turmaId,
        @Param("horarioId") Long horarioId,
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim
    );

    @Query(
        value = """
            SELECT EXISTS (
                SELECT 1
                FROM professor_turma pt
                JOIN professor_turma outra_pt
                    ON outra_pt.id_professor = pt.id_professor
                    AND outra_pt.id_turma <> pt.id_turma
                    AND outra_pt.status = 'ATIVO'
                JOIN turma outra_turma ON outra_turma.id = outra_pt.id_turma
                JOIN horario_intervalo horario_atual
                    ON horario_atual.id_horario = :horarioId
                JOIN horario_intervalo outro_horario
                    ON outro_horario.id_horario = outra_turma.horario_id
                WHERE pt.id_turma = :turmaId
                    AND pt.status = 'ATIVO'
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

    @Modifying
    @Query(
        value = """
            UPDATE professor_turma
            SET status = 'ENCERRADO', data_saida = :dataSaida
            WHERE id_turma = :turmaId AND status = 'ATIVO'
            """,
        nativeQuery = true
    )
    int encerrarVinculosAtivos(@Param("turmaId") Long turmaId, @Param("dataSaida") LocalDate dataSaida);

    @Modifying
    @Query(
        value = """
            UPDATE professor_turma pt
            SET status = 'ENCERRADO', data_saida = t.data_fim
            FROM turma t
            WHERE pt.id_turma = t.id
                AND pt.status = 'ATIVO'
                AND t.data_termino IS NULL
                AND t.data_fim <= CURRENT_DATE
            """,
        nativeQuery = true
    )
    int encerrarVinculosDeTurmasVencidas();

    @Query(
        value = """
                SELECT DISTINCT pt.id_professor
                FROM professor_turma pt
                WHERE pt.id_professor IN (:professorIds)
                    AND pt.status = 'ATIVO'
                    AND pt.data_entrada <= CURRENT_DATE
                    AND (
                        pt.data_saida IS NULL
                        OR pt.data_saida > CURRENT_DATE
                    )
                """,
        nativeQuery = true
    )
    List<UUID> findProfessorIdsComTurmaAtiva(@Param("professorIds") Collection<UUID> professorIds);

    @Query(
            value = """
                    SELECT DISTINCT
                        pt.id_professor AS "professorId",
                        i.nome AS instrumento
                    FROM professor_turma pt
                    JOIN instrumento_turma it ON it.id_turma = pt.id_turma
                    JOIN instrumento i ON i.id = it.id_instrumento
                    WHERE pt.id_professor IN (:professorIds)
                        AND pt.status = 'ATIVO'
                        AND pt.data_entrada <= CURRENT_DATE
                        AND (
                            pt.data_saida IS NULL
                            OR pt.data_saida > CURRENT_DATE
                        )
                        AND EXISTS (
                            SELECT 1
                            FROM membro_vinculo mv
                            JOIN funcao f ON f.id = mv.id_funcao
                            WHERE mv.id_membro = pt.id_professor
                                AND f.code = 'PROFESSOR'
                                AND mv.deletado_em IS NULL
                                AND mv.data_inicio <= CURRENT_DATE
                                AND (
                                    mv.data_termino IS NULL
                                    OR mv.data_termino > CURRENT_DATE
                                )
                        )
                    """,
            nativeQuery = true
    )
    List<ProfessorInstrumentoProjection> findInstrumentosEnsinados(
            @Param("professorIds") Collection<UUID> professorIds
    );


    @Query("""
        SELECT pt
        FROM ProfessorTurma pt
        WHERE pt.professor.id = :idProfessor
            AND (:status IS NULL OR pt.status = :status)
    """)
    Page<ProfessorTurma> listarPorProfessorEStatus(
            @Param("idProfessor") UUID idProfessor,
            @Param("status") ProfessorTurma.EnumStatusTurma status,
            Pageable pageable
    );

}
