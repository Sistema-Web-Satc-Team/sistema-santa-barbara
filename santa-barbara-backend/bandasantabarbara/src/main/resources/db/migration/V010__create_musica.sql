CREATE TABLE musica(
    id UUID,
    titulo VARCHAR(256) NOT NULL,
    compositor VARCHAR(256),
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,

    CONSTRAINT pk_musica PRIMARY KEY (id)
);
