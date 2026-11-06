-- Permite associar um ou mais instrumentos a uma turma.

CREATE TABLE instrumento_turma (
    id_turma BIGINT NOT NULL,
    id_instrumento INT NOT NULL,

    CONSTRAINT pk_instrumento_turma
        PRIMARY KEY (id_turma, id_instrumento),

    CONSTRAINT fk_instrumento_turma_turma
        FOREIGN KEY (id_turma)
        REFERENCES turma (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_instrumento_turma_instrumento
        FOREIGN KEY (id_instrumento)
        REFERENCES instrumento (id)
        ON DELETE RESTRICT
);

-- A PK já indexa id_turma como primeira coluna.
-- Este índice atende consultas no sentido instrumento -> turmas.
CREATE INDEX idx_instrumento_turma_id_instrumento
    ON instrumento_turma (id_instrumento);
