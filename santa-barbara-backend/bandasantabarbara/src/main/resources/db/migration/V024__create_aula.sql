CREATE TABLE aula (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    id_turma BIGINT NOT NULL,
    id_professor UUID NOT NULL,
    data_hora_inicio TIMESTAMPTZ NOT NULL,
    data_hora_fim_prevista TIMESTAMPTZ NOT NULL,
    data_hora_fim TIMESTAMPTZ,
    descricao TEXT,
    tipo_encerramento VARCHAR(20),
    status VARCHAR(20) NOT NULL DEFAULT 'EM_ANDAMENTO',

    CONSTRAINT pk_aula PRIMARY KEY (id),

    CONSTRAINT fk_aula_turma
        FOREIGN KEY (id_turma)
        REFERENCES turma (id),

    CONSTRAINT fk_aula_professor
        FOREIGN KEY (id_professor)
        REFERENCES professor (id_membro)
);

CREATE INDEX idx_aula_id_turma
    ON aula (id_turma);

CREATE INDEX idx_aula_status_fim_prevista
    ON aula (status, data_hora_fim_prevista);