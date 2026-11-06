CREATE TABLE convite (
    id UUID NOT NULL,
    id_membro UUID NOT NULL,
    data_convite_criacao TIMESTAMP NOT NULL,
    data_convite_expiracao TIMESTAMP,
    status VARCHAR(32) NOT NULL,


    CONSTRAINT pk_convite PRIMARY KEY (id),

    CONSTRAINT fk_convite_membro FOREIGN KEY (id_membro)
        REFERENCES membro(id)
);

