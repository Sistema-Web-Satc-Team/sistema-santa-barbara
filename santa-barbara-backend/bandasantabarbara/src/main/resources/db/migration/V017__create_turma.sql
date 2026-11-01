-- A obrigação de certos campos depende das regras de negócio
CREATE TABLE turma(
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    nome VARCHAR(126),
    sala_id INT,
    horario_id BIGINT,
    data_inicio DATE,
    data_fim DATE,
    data_termino DATE,
    status VARCHAR(20),
    numero_vagas INT,
    turno VARCHAR(14),

    CONSTRAINT pk_turma PRIMARY KEY (id),
    CONSTRAINT fk_sala FOREIGN KEY (sala_id) REFERENCES sala(id),
    CONSTRAINT fk_horario FOREIGN KEY (horario_id) REFERENCES horario(id),
    CONSTRAINT uq_turma_horario UNIQUE (horario_id)
);