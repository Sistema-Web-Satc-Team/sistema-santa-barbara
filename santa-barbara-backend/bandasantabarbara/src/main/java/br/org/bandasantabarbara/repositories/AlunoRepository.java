package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Aluno;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/*
*
* Débito técnico: Acoplamento com SGDB PostgreSQL.
*
 */

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Integer> {


    @Query(
        value =
        """
        SELECT
            m.id AS id,
            m.nome_legal AS nome,
            m.email AS email,
            m.telefone AS telefone,
            m.sexo AS sexo,
            m.data_nascimento AS dataNascimento,
            mv.data_inicio AS dataInicio,
            mv.data_termino AS dataTermino,
            mv.deletado_em AS deletadoEm
        FROM aluno a
        JOIN membro_vinculo mv ON mv.id = a.id_vinculo
        JOIN membro m ON m.id = mv.id_membro
        WHERE
            CASE :status
                WHEN 'ATIVO' THEN
                    mv.deletado_em IS NULL
                    AND mv.data_inicio <= CURRENT_DATE
                    AND (
                        mv.data_termino IS NULL
                        OR mv.data_termino > CURRENT_DATE
                    )
                WHEN 'AGENDADO' THEN
                    mv.deletado_em IS NULL
                    AND mv.data_inicio > CURRENT_DATE
                    AND mv.data_termino IS NULL
                WHEN 'CANCELADO' THEN
                    mv.deletado_em IS NOT NULL
                WHEN 'ENCERRADO' THEN
                    mv.deletado_em IS NULL
                    AND mv.data_termino IS NOT NULL
                    AND mv.data_termino <= CURRENT_DATE
                ELSE FALSE
            END
        """,
        countQuery = """
            SELECT COUNT(*)
                FROM aluno a
                JOIN membro_vinculo mv ON mv.id = a.id_vinculo
            WHERE
                CASE :status
                    WHEN 'ATIVO' THEN
                        mv.deletado_em IS NULL
                        AND mv.data_inicio <= CURRENT_DATE
                        AND (
                            mv.data_termino IS NULL
                            OR mv.data_termino > CURRENT_DATE
                        )
                    WHEN 'AGENDADO' THEN
                        mv.deletado_em IS NULL
                        AND mv.data_inicio > CURRENT_DATE
                        AND mv.data_termino IS NULL
                    WHEN 'CANCELADO' THEN
                        mv.deletado_em IS NOT NULL
                    WHEN 'ENCERRADO' THEN
                        mv.deletado_em IS NULL
                        AND mv.data_termino IS NOT NULL
                        AND mv.data_termino <= CURRENT_DATE
                    ELSE FALSE
                END
        """,
            nativeQuery = true
    )
    Page<AlunoProjection> buscarPorStatus(
            @Param("status") String status,
            Pageable pageable
    );


}
