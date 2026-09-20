package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.application.filters.MembroFilter;
import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.model.MembroCredencial;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembroRepository extends Repository<Membro, UUID> {

    Membro save(Membro membro);

    Optional<Membro> findById(UUID id);

    @Query("""
        SELECT DISTINCT m FROM Membro m 
        LEFT JOIN m.papeis p
        WHERE 
            (
                :#{#filter.nome} IS NULL
                OR 
                LOWER(
                    CONCAT(
                        COALESCE(m.nome, ''), 
                            ' ', 
                        COALESCE(m.sobrenome, '')
                    )
                ) 
                LIKE 
                LOWER(
                    CONCAT(
                        '%', 
                        :#{#filter.nome}, 
                        '%'
                    )
                )
            )
        AND
            (
                :#{#filter.papeis} IS NULL
                OR 
                p.nome IN :#{#filter.papeis}
            )          
    """)
    List<Membro> listar(Pageable pageable, @Param("filter") MembroFilter filter);


    @Query("""
        SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END 
        FROM Membro m 
        JOIN m.papeis p 
        WHERE p.nome = 'SUPER_ADMIN'
    """)
    boolean existeAdminCadastrado();

    @Query("""
        SELECT m FROM Membro m
        JOIN m.credencial c
        WHERE m.nomeDeUsuario = :login OR m.email = :login
    """)
    Optional<Membro> findByUsernameOuEmail(@Param("login") String login);

    @Query("""
        SELECT m FROM Membro m
        JOIN m.credencial c
        WHERE m.nomeDeUsuario = :login OR m.email = :login
    """)
    @EntityGraph(attributePaths = {"papeis.permissoes"})
    Optional<Membro> findByUsernameOuEmailWithPapel(@Param("login") String login);
}
