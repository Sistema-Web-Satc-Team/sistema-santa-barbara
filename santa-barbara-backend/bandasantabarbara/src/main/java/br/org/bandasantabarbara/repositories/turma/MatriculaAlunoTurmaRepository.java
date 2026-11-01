package br.org.bandasantabarbara.repositories.turma;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import br.org.bandasantabarbara.model.turma.MatriculaAlunoTurma;
import br.org.bandasantabarbara.repositories.ensino.AlunoInstrumentoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatriculaAlunoTurmaRepository extends JpaRepository<MatriculaAlunoTurma, Long> {

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
}
