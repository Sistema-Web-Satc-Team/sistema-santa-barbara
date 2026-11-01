
-- Registra as atribuições e o histórico de professores por turma.
CREATE TABLE professor_turma (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    id_turma BIGINT NOT NULL,
    id_professor UUID NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ATIVO',
    justificativa_mudanca_status TEXT,
    data_entrada DATE NOT NULL,
    data_saida DATE,

    CONSTRAINT pk_professor_turma
        PRIMARY KEY (id),

    CONSTRAINT fk_professor_turma_turma
        FOREIGN KEY (id_turma)
        REFERENCES turma (id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_professor_turma_professor
        FOREIGN KEY (id_professor)
        REFERENCES professor (id_membro)
        ON DELETE RESTRICT,

    CONSTRAINT chk_data_professor_turma
        CHECK (data_saida IS NULL OR data_saida > data_entrada)
);

CREATE INDEX idx_professor_turma_id_turma
    ON professor_turma (id_turma);

CREATE INDEX idx_professor_turma_id_professor
    ON professor_turma (id_professor);
