
CREATE TABLE professor (
    id_membro UUID NOT NULL,

    CONSTRAINT fk_professor_membro
        FOREIGN KEY (id_membro)
        REFERENCES membro(id)
        ON DELETE RESTRICT,

    CONSTRAINT pk_professor PRIMARY KEY (id_membro)
);