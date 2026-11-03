package br.org.bandasantabarbara.repositories.aula;

import br.org.bandasantabarbara.model.aula.Chamada;
import br.org.bandasantabarbara.model.aula.ChamadaId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.EntityGraph;

public interface ChamadaRepository extends JpaRepository<Chamada, ChamadaId> {

    @Query("""
        SELECT chamada FROM Chamada chamada
        JOIN FETCH chamada.matriculaAlunoTurma matricula
        JOIN FETCH matricula.aluno aluno
        JOIN FETCH aluno.membro
        WHERE chamada.aula.id = :aulaId
        ORDER BY aluno.membro.nome
    """)
    List<Chamada> listarPorAula(@Param("aulaId") Long aulaId);

    @EntityGraph(attributePaths = {"matriculaAlunoTurma", "matriculaAlunoTurma.turma"})
    List<Chamada> findAllByAula_Id(Long aulaId);
}
