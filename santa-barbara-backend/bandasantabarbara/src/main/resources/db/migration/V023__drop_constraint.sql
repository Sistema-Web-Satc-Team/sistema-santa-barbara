-- Backend decide não banco.

DROP INDEX IF EXISTS uq_vinculo_ativo;

ALTER TABLE professor_turma
    DROP CONSTRAINT chk_data_professor_turma;

ALTER TABLE matricula_aluno_turma
    DROP CONSTRAINT chk_data_matricula_aluno_turma;