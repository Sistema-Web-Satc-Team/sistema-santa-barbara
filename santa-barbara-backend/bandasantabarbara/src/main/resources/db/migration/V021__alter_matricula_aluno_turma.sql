ALTER TABLE matricula_aluno_turma
    DROP CONSTRAINT chk_data_matricula_aluno_turma;

ALTER TABLE matricula_aluno_turma
    ADD CONSTRAINT chk_data_matricula_aluno_turma
        CHECK (data_saida IS NULL OR data_saida >= data_matricula);

