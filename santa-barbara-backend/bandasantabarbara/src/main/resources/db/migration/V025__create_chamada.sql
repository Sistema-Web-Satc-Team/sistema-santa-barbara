CREATE TABLE chamada (
    id_aula BIGINT NOT NULL,
    id_matricula_aluno BIGINT NOT NULL,
    presente BOOLEAN NOT NULL,

    CONSTRAINT pk_chamada
        PRIMARY KEY (id_aula, id_matricula_aluno),

    CONSTRAINT fk_chamada_aula
        FOREIGN KEY (id_aula)
        REFERENCES aula (id),

    CONSTRAINT fk_chamada_matricula_aluno
        FOREIGN KEY (id_matricula_aluno)
        REFERENCES matricula_aluno_turma (id)
);
