package br.org.bandasantabarbara.repositories.ensino;

import java.util.Collection;
import java.util.UUID;

import br.org.bandasantabarbara.model.ensino.Professor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProfessorRepository extends JpaRepository<Professor, UUID> {

    @Query(
            value = """
                    SELECT
                        p.id_membro AS id,
                        m.nome_legal AS nome,
                        m.email AS email,
                        m.telefone AS telefone,
                        m.sexo AS sexo
                    FROM professor p
                    JOIN membro m ON m.id = p.id_membro
                    WHERE EXISTS (
                        SELECT 1
                        FROM membro_vinculo mv
                        JOIN funcao f ON f.id = mv.id_funcao
                        WHERE mv.id_membro = p.id_membro
                            AND f.code = 'PROFESSOR'
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
                        :filtrarInstrumentos = FALSE
                        OR EXISTS (
                            SELECT 1
                            FROM professor_turma pt
                            JOIN instrumento_turma it ON it.id_turma = pt.id_turma
                            JOIN instrumento i ON i.id = it.id_instrumento
                            WHERE pt.id_professor = p.id_membro
                                AND pt.status = 'ATIVO'
                                AND pt.data_entrada <= CURRENT_DATE
                                AND (
                                    pt.data_saida IS NULL
                                    OR pt.data_saida > CURRENT_DATE
                                )
                                AND LOWER(i.nome) IN (:instrumentosEnsinando)
                        )
                    )
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM professor p
                    JOIN membro m ON m.id = p.id_membro
                    WHERE EXISTS (
                        SELECT 1
                        FROM membro_vinculo mv
                        JOIN funcao f ON f.id = mv.id_funcao
                        WHERE mv.id_membro = p.id_membro
                            AND f.code = 'PROFESSOR'
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
                        :filtrarInstrumentos = FALSE
                        OR EXISTS (
                            SELECT 1
                            FROM professor_turma pt
                            JOIN instrumento_turma it ON it.id_turma = pt.id_turma
                            JOIN instrumento i ON i.id = it.id_instrumento
                            WHERE pt.id_professor = p.id_membro
                                AND pt.status = 'ATIVO'
                                AND pt.data_entrada <= CURRENT_DATE
                                AND (
                                    pt.data_saida IS NULL
                                    OR pt.data_saida > CURRENT_DATE
                                )
                                AND LOWER(i.nome) IN (:instrumentosEnsinando)
                        )
                    )
                    """,
            nativeQuery = true
    )
    Page<ProfessorProjection> buscarProfessores(
            @Param("search") String search,
            @Param("filtrarInstrumentos") boolean filtrarInstrumentos,
            @Param("instrumentosEnsinando") Collection<String> instrumentosEnsinando,
            Pageable pageable
    );
}
