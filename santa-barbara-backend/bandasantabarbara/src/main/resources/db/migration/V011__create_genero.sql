CREATE TABLE genero(
    id INT GENERATED ALWAYS AS IDENTITY NOT NULL,
    nome VARCHAR(256) NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,
    slug VARCHAR(128) NOT NULL,

    CONSTRAINT pk_genero PRIMARY KEY (id)
);

CREATE TABLE genero_musica(
    id_musica UUID NOT NULL,
    id_genero INT NOT NULL,

    CONSTRAINT pk_genero_musica PRIMARY KEY (id_musica, id_genero),

    CONSTRAINT fk_genero_musica_musica FOREIGN KEY (id_musica)
        REFERENCES musica(id),

    CONSTRAINT fk_genero_musica_genero FOREIGN KEY (id_genero)
        REFERENCES genero(id)
);