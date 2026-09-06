package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Membro;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class SetupApplicationRepositoryImpl implements SetupApplicationRepository {

    private final EntityManager entityManager;

    @Override
    @Transactional
    public void salvarAdmin(Membro membro) {

        String sql = """
            WITH
            papel_admin AS (
                INSERT INTO papel (nome, criado_em, atualizado_em)
                VALUES (:papel, NOW(), NOW())
                ON CONFLICT (nome) DO UPDATE SET nome = EXCLUDED.nome
                RETURNING id
            ),
            novo_membro AS (
                INSERT INTO membro (
                    id,
                    email,
                    nome_usuario,
                    criado_em,
                    atualizado_em
                )
                VALUES (
                    :membroId,
                    :email,
                    :username,
                    NOW(),
                    NOW()
                )
                RETURNING id
            ),
            nova_credencial AS (
                INSERT INTO membro_credencial (
                    membro_id,
                    hash_password,
                    criado_em,
                    atualizado_em
                )
                SELECT
                    id,
                    :hashSenha,
                    NOW(),
                    NOW()
                FROM novo_membro
            )
            INSERT INTO membro_papel (id_membro, id_papel)
            SELECT
                nm.id,
                pa.id
            FROM novo_membro nm
            CROSS JOIN papel_admin pa;
            """;

        var papel = membro.getPapeis().iterator().next();

        entityManager.createNativeQuery(sql)
                .setParameter("membroId", membro.getId())
                .setParameter("email", membro.getEmail())
                .setParameter("username", membro.getNomeDeUsuario())
                .setParameter(
                        "hashSenha",
                        membro.getCredencial().getHashSenha()
                )
                .setParameter("papel", papel.getNome())
                .executeUpdate();
    }
}