package br.org.bandasantabarbara.repositories.aula;

import br.org.bandasantabarbara.model.aula.Aula;
import br.org.bandasantabarbara.model.aula.Aula.EnumTipoEncerramento;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AulaRepository extends JpaRepository<Aula, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT aula FROM Aula aula
        WHERE aula.id = :aulaId AND aula.turma.id = :turmaId
    """)
    Optional<Aula> findByIdAndTurmaIdForUpdate(
        @Param("aulaId") Long aulaId,
        @Param("turmaId") Long turmaId
    );

    Optional<Aula> findByIdAndTurma_Id(Long aulaId, Long turmaId);

    @Query("""
        SELECT aula FROM Aula aula
        WHERE aula.turma.id = :turmaId
            AND (:tipoEncerramento IS NULL OR aula.tipoEncerramento = :tipoEncerramento)
    """)
    Page<Aula> listarPorTurmaEEncerramento(
        @Param("turmaId") Long turmaId,
        @Param("tipoEncerramento") EnumTipoEncerramento tipoEncerramento,
        Pageable pageable
    );

    @Modifying
    @Query(
        value = """
            UPDATE aula
            SET status = 'FINALIZADA',
                data_hora_fim = data_hora_fim_prevista,
                tipo_encerramento = 'TEMPO_ESGOTADO'
            WHERE status = 'EM_ANDAMENTO'
                AND data_hora_fim_prevista <= :agora
        """,
        nativeQuery = true
    )
    int encerrarAulasVencidas(@Param("agora") LocalDateTime agora);
}
