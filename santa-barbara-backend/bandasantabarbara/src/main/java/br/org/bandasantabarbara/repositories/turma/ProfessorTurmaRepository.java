package br.org.bandasantabarbara.repositories.turma;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

import br.org.bandasantabarbara.model.turma.ProfessorTurma;
import br.org.bandasantabarbara.repositories.ensino.ProfessorInstrumentoProjection;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProfessorTurmaRepository extends JpaRepository<ProfessorTurma, Long> {

    boolean existsByTurma_IdAndProfessor_IdAndStatus(Long turmaId, UUID professorId, String status);

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
}
