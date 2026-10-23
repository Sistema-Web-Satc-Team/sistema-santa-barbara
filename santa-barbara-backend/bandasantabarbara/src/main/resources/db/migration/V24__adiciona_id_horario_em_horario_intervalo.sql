ALTER TABLE horario_intervalo
    ADD COLUMN id_horario BIGINT,
    ADD CONSTRAINT horario_intervalo_horario_fk
        FOREIGN KEY (id_horario) REFERENCES horario (id) ON DELETE CASCADE;

CREATE INDEX idx_horario_intervalo_id_horario ON horario_intervalo (id_horario);