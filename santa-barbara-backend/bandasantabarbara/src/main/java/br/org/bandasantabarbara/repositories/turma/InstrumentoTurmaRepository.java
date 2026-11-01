package br.org.bandasantabarbara.repositories.turma;

import br.org.bandasantabarbara.model.turma.InstrumentoTurma;
import br.org.bandasantabarbara.model.turma.InstrumentoTurmaId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InstrumentoTurmaRepository extends JpaRepository<InstrumentoTurma, InstrumentoTurmaId> {

    @Modifying
    @Query(value = "DELETE FROM instrumento_turma WHERE id_turma = :turmaId", nativeQuery = true)
    void excluirPorTurma(@Param("turmaId") Long turmaId);

    List<InstrumentoTurma> findByTurma_Id(Long turmaId);
}
