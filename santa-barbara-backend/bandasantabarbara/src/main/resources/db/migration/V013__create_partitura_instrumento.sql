CREATE TABLE partitura_instrumento (
    id_partitura UUID NOT NULL,
    id_instrumento INT NOT NULL,

    CONSTRAINT pk_partitura_instrumento PRIMARY KEY (id_partitura, id_instrumento),

    CONSTRAINT fk_partitura_instrumento_partitura FOREIGN KEY (id_partitura)
        REFERENCES partitura(id),
    
    CONSTRAINT fk_partitura_instrumento_instrumento FOREIGN KEY (id_instrumento)
        REFERENCES instrumento(id)
);