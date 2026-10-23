
CREATE TABLE turma(
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    nome VARCHAR(126),
    sala_id INT,
    horario_id BIGINT,
    data_inicio DATE,
    data_fim DATE,
    data_termino DATE,
    stats VARCHAR(20),

    CONSTRAINT pk_turma PRIMARY KEY (id),
    CONSTRAINT fk_sala FOREIGN KEY (sala_id) REFERENCES sala(id),
    CONSTRAINT fk_horario FOREIGN KEY (horario_id) REFERENCES horario(id),
    CONSTRAINT chk_uq_horario UNIQUE (horario_id)
);