CREATE TABLE membro(
    id UUID NOT NULL,
    avatar_file_id UUID,
    nome_legal VARCHAR(72) NOT NULL,
    data_nascimento DATE NOT NULL,
    sexo VARCHAR(32) NOT NULL,
    endereco VARCHAR(256),
    telefone VARCHAR(15),
    email VARCHAR(256),
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,

    CONSTRAINT pk_membro PRIMARY KEY (id),
    CONSTRAINT fk_membro_avatar_file FOREIGN KEY (avatar_file_id) REFERENCES file(id),

    CONSTRAINT uq_membro_avatar_file UNIQUE (avatar_file_id)
);

CREATE TABLE membro_credencial(
    id_membro UUID NOT NULL,
    nome_usuario VARCHAR(32),
    hash_password VARCHAR(256) NOT NULL,
    status VARCHAR(64) NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,

    CONSTRAINT pk_membro_credencial PRIMARY KEY (id_membro),
    CONSTRAINT fk_membro_credencial_membro FOREIGN KEY (id_membro) REFERENCES membro(id)
);

CREATE TABLE membro_responsavel (
    id_membro_responsavel UUID NOT NULL,
    id_membro_dependente UUID NOT NULL,
    tipo_relacao VARCHAR(72) NOT NULL,

    CONSTRAINT pk_membro_responsavel 
        PRIMARY KEY (id_membro_responsavel, id_membro_dependente),
    
    CONSTRAINT fk_membro_responsavel_membro 
        FOREIGN KEY (id_membro_responsavel)
            REFERENCES membro(id),
    
    CONSTRAINT fk_membro_dependente_membro 
        FOREIGN KEY (id_membro_dependente)
            REFERENCES membro(id),
    
    CONSTRAINT chk_membro_diferente 
        CHECK (id_membro_responsavel != id_membro_dependente)

);