CREATE TABLE horario (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    CONSTRAINT pk_horario PRIMARY KEY (id)
);

CREATE TABLE horario_intervalo(
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    id_horario BIGINT NOT NULL,
    dia_semana VARCHAR(13) NOT NULL,
    inicio TIME NOT NULL,
    fim TIME NOT NULL,

    CONSTRAINT pk_horario_intervalo PRIMARY KEY (id),

    CONSTRAINT fk_horario_intervalo_horario FOREIGN KEY (id_horario)
        REFERENCES horario (id) ON DELETE CASCADE,

    CONSTRAINT chk_horario_intervalo_periodo CHECK (fim > inicio) 
);
