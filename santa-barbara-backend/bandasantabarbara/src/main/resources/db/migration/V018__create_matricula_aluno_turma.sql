
-- Registra o histórico de matrículas de alunos em turmas.

CREATE TABLE matricula_aluno_turma (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    id_turma BIGINT NOT NULL,
    id_aluno UUID NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ATIVO',
    justificativa_mudanca_status TEXT,
    data_matricula DATE NOT NULL,
    data_saida DATE,

    CONSTRAINT pk_matricula_aluno_turma
        PRIMARY KEY (id),

    CONSTRAINT fk_matricula_aluno_turma_turma
        FOREIGN KEY (id_turma)
        REFERENCES turma (id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_matricula_aluno_turma_aluno
        FOREIGN KEY (id_aluno)
        REFERENCES aluno (id_membro)
        ON DELETE RESTRICT,

    CONSTRAINT chk_data_matricula_aluno_turma
        CHECK (data_saida IS NULL OR data_saida > data_matricula)
);

CREATE INDEX idx_matricula_aluno_turma_id_turma
    ON matricula_aluno_turma (id_turma);

CREATE INDEX idx_matricula_aluno_turma_id_aluno
    ON matricula_aluno_turma (id_aluno);

-- Preserva o histórico e impede duas matrículas ATIVAS do mesmo aluno
-- na mesma turma. Uma nova matrícula após a saída gera outra linha.
-- Isso não deve substituir a lógica de negócio.
CREATE UNIQUE INDEX uq_matricula_aluno_turma_ativa
    ON matricula_aluno_turma (id_turma, id_aluno)
    WHERE status = 'ATIVO';
