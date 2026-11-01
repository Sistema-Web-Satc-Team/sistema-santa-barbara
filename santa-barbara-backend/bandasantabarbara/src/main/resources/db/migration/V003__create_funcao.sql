CREATE TABLE funcao (
    id INT GENERATED ALWAYS AS IDENTITY NOT NULL,
    code VARCHAR(32) NOT NULL,
    nome VARCHAR(64) NOT NULL,
    descricao TEXT,
    tipo VARCHAR(32),

    CONSTRAINT pk_funcao PRIMARY KEY (id),

    CONSTRAINT uq_funcao_code UNIQUE (code)
);


