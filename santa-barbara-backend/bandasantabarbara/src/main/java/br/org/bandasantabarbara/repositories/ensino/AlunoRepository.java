package br.org.bandasantabarbara.repositories.ensino;

import java.util.UUID;
import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;

import br.org.bandasantabarbara.model.ensino.Aluno;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Aluno a WHERE a.id = :id")
    java.util.Optional<Aluno> findByIdForUpdate(@Param("id") UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT a FROM Aluno a
        WHERE a.id IN (
            SELECT matricula.aluno.id
            FROM MatriculaAlunoTurma matricula
            WHERE matricula.turma.id = :turmaId
                AND matricula.status = 'ATIVO'
        )
        ORDER BY a.id
    """)
    java.util.List<Aluno> findActiveStudentsByTurmaForUpdate(@Param("turmaId") Long turmaId);

    @Query(
        value = """
            SELECT
                a.id_membro AS id,
                m.nome_legal AS nome,
                m.email AS email,
                m.telefone AS telefone,
                m.sexo AS sexo,
                m.data_nascimento AS "dataNascimento"
            FROM aluno a
            JOIN membro m ON m.id = a.id_membro
            WHERE EXISTS (
                SELECT 1
                FROM membro_vinculo mv
                JOIN funcao f ON f.id = mv.id_funcao
                WHERE mv.id_membro = a.id_membro
                    AND f.code = 'ALUNO'
                    AND mv.deletado_em IS NULL
                    AND mv.data_inicio <= CURRENT_DATE
                    AND (
                        mv.data_termino IS NULL
                        OR mv.data_termino > CURRENT_DATE
                    )
            )
            AND (
                :search IS NULL
                OR LOWER(m.nome_legal) LIKE CONCAT('%', LOWER(:search), '%')
                OR LOWER(COALESCE(m.email, '')) LIKE CONCAT('%', LOWER(:search), '%')
            )
            AND (
                :apenasMenores = FALSE
                OR m.data_nascimento > CURRENT_DATE - INTERVAL '18 years'
            )
            AND (
                :filtrarInstrumentos = FALSE
                OR EXISTS (
                    SELECT 1
                    FROM matricula_aluno_turma mat
                    JOIN instrumento_turma it ON it.id_turma = mat.id_turma
                    JOIN instrumento i ON i.id = it.id_instrumento
                    WHERE mat.id_aluno = a.id_membro
                        AND mat.status = 'ATIVO'
                        AND mat.data_matricula <= CURRENT_DATE
                        AND (
                            mat.data_saida IS NULL
                            OR mat.data_saida > CURRENT_DATE
                        )
                        AND LOWER(i.nome) IN (:instrumentosAprendendo)
                )
            )
            """,
        countQuery = """
            SELECT COUNT(*)
            FROM aluno a
            JOIN membro m ON m.id = a.id_membro
            WHERE EXISTS (
                SELECT 1
                FROM membro_vinculo mv
                JOIN funcao f ON f.id = mv.id_funcao
                WHERE mv.id_membro = a.id_membro
                    AND f.code = 'ALUNO'
                    AND mv.deletado_em IS NULL
                    AND mv.data_inicio <= CURRENT_DATE
                    AND (
                        mv.data_termino IS NULL
                        OR mv.data_termino > CURRENT_DATE
                    )
            )
            AND (
                :search IS NULL
                OR LOWER(m.nome_legal) LIKE CONCAT('%', LOWER(:search), '%')
                OR LOWER(COALESCE(m.email, '')) LIKE CONCAT('%', LOWER(:search), '%')
            )
            AND (
                :apenasMenores = FALSE
                OR m.data_nascimento > CURRENT_DATE - INTERVAL '18 years'
            )
            AND (
                :filtrarInstrumentos = FALSE
                OR EXISTS (
                    SELECT 1
                    FROM matricula_aluno_turma mat
                    JOIN instrumento_turma it ON it.id_turma = mat.id_turma
                    JOIN instrumento i ON i.id = it.id_instrumento
                    WHERE mat.id_aluno = a.id_membro
                        AND mat.status = 'ATIVO'
                        AND mat.data_matricula <= CURRENT_DATE
                        AND (
                            mat.data_saida IS NULL
                            OR mat.data_saida > CURRENT_DATE
                        )
                        AND LOWER(i.nome) IN (:instrumentosAprendendo)
                )
            )
            """,
        nativeQuery = true
    )
    Page<AlunoProjection> buscarAlunos(
            @Param("search") String search,
            @Param("filtrarInstrumentos") boolean filtrarInstrumentos,
            @Param("instrumentosAprendendo") Collection<String> instrumentosAprendendo,
            @Param("apenasMenores") boolean apenasMenores,
            Pageable pageable
    );
}
