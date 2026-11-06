
CREATE TABLE aluno (
    id_membro UUID NOT NULL,

    CONSTRAINT fk_aluno_membro
        FOREIGN KEY (id_membro)
        REFERENCES membro(id)
        ON DELETE RESTRICT,

    CONSTRAINT pk_aluno PRIMARY KEY (id_membro)
);