
CREATE TABLE sala (
    id INT GENERATED ALWAYS AS IDENTITY,
    code VARCHAR(128) NOT NULL,
    capacidade INT NOT NULL,

    CONSTRAINT sala_pk PRIMARY KEY (id),
    CONSTRAINT chk_capacidade CHECK (capacidade > 0)
);