
CREATE TABLE IF NOT EXISTS permissao (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome VARCHAR(60) UNIQUE NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL
);


CREATE TABLE IF NOT EXISTS papel_permissao (
    id_papel INT NOT NULL REFERENCES papel(id) ON DELETE CASCADE,
    id_permissao INT NOT NULL REFERENCES permissao(id) ON DELETE CASCADE,
    PRIMARY KEY (id_papel, id_permissao)
);

INSERT INTO papel (nome, criado_em, atualizado_em) VALUES 
('MAESTRO', NOW(), NOW()),
('ALUNO', NOW(), NOW()),
('PROFESSOR', NOW(), NOW()),
('SECRETARIO', NOW(), NOW());

INSERT INTO permissao (nome, criado_em, atualizado_em) VALUES 
('MEMBRO:CADASTRAR', NOW(), NOW()),
('MEMBRO:VISUALIZAR', NOW(), NOW()),
('MEMBRO:ATUALIZAR', NOW(), NOW()),
('MEMBRO:REMOVER', NOW(), NOW());


CREATE OR REPLACE PROCEDURE vincular_papel_permissao(
    p_nome_papel VARCHAR,
    p_nome_permissao VARCHAR
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_id_papel INT;
    v_id_permissao INT;
BEGIN
    SELECT id INTO v_id_papel FROM papel WHERE nome = p_nome_papel;
    
    SELECT id INTO v_id_permissao FROM permissao WHERE nome = p_nome_permissao;

    IF v_id_papel IS NULL THEN
        RAISE EXCEPTION 'Papel % não encontrado!', p_nome_papel;
    END IF;
    
    IF v_id_permissao IS NULL THEN
        RAISE EXCEPTION 'Permissão % não encontrada!', p_nome_permissao;
    END IF;

    INSERT INTO papel_permissao (id_papel, id_permissao)
    VALUES (v_id_papel, v_id_permissao)
    ON CONFLICT (id_papel, id_permissao) DO NOTHING;

END;
$$;

CALL vincular_papel_permissao('SECRETARIO', 'MEMBRO:CADASTRAR');
CALL vincular_papel_permissao('SECRETARIO', 'MEMBRO:VISUALIZAR');
CALL vincular_papel_permissao('SECRETARIO', 'MEMBRO:ATUALIZAR');

CALL vincular_papel_permissao('ALUNO', 'MEMBRO:VISUALIZAR');
CALL vincular_papel_permissao('MAESTRO', 'MEMBRO:VISUALIZAR');
CALL vincular_papel_permissao('PROFESSOR', 'MEMBRO:VISUALIZAR');

CALL vincular_papel_permissao('SUPER_ADMIN', 'MEMBRO:VISUALIZAR');
CALL vincular_papel_permissao('SUPER_ADMIN', 'MEMBRO:CADASTRAR');
CALL vincular_papel_permissao('SUPER_ADMIN', 'MEMBRO:ATUALIZAR');
CALL vincular_papel_permissao('SUPER_ADMIN', 'MEMBRO:REMOVER');


