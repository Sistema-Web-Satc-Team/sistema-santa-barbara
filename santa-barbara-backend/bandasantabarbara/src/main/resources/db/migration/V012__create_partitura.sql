CREATE TABLE partitura(
    id UUID NOT NULL,
    id_musica UUID NOT NULL,
    titulo VARCHAR(256) NOT NULL,
    arranjador VARCHAR(256),
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,
    partitura_file_id UUID,


    CONSTRAINT pk_partitura PRIMARY KEY (id),

    CONSTRAINT fk_partitura_musica FOREIGN KEY (id_musica)
        REFERENCES musica(id),
    
    CONSTRAINT fk_partitura_file FOREIGN KEY (partitura_file_id)
        REFERENCES file(id)

);