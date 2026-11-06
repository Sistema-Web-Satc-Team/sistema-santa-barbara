ALTER TABLE professor_turma
    DROP CONSTRAINT chk_data_professor_turma;

ALTER TABLE professor_turma
    ADD CONSTRAINT chk_data_professor_turma
        CHECK (data_saida IS NULL OR data_saida >= data_entrada);
