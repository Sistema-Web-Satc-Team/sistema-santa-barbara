CREATE TABLE instrumento(
    id INT GENERATED ALWAYS AS IDENTITY NOT NULL,
    nome VARCHAR(256) NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,
    file_id UUID,
    descricao TEXT,
    tipo VARCHAR(64) NOT NULL,

    CONSTRAINT pk_instrumento PRIMARY KEY (id),

    CONSTRAINT fk_instrumento_file FOREIGN KEY (file_id)
        REFERENCES file(id)
);