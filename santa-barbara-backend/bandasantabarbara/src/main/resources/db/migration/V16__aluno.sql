
CREATE TABLE aluno(
    id_vinculo UUID PRIMARY KEY,

    CONSTRAINT fk_aluno_membro_vinculo
        FOREIGN KEY (id_vinculo)
        REFERENCES membro_vinculo(id)
        ON DELETE CASCADE
);



