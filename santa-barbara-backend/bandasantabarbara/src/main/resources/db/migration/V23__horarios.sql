CREATE TABLE horario (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    CONSTRAINT horario_pk PRIMARY KEY (id)
);

CREATE TABLE horario_intervalo(
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    dia_semana VARCHAR(13),
    inicio TIME,
    fim TIME,

    CONSTRAINT horario_intervalo_pk PRIMARY KEY (id)
);