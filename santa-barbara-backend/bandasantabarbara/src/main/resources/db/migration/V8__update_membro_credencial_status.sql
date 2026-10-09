CREATE TYPE status_membro_credencial_novo AS ENUM ('ATIVO', 'SUSPENSO', 'BLOQUEADO');

ALTER TABLE membro_credencial
  ALTER COLUMN status TYPE status_membro_credencial_novo
  USING (
    CASE
      WHEN status::text = 'INATIVO' THEN 'BLOQUEADO'::status_membro_credencial_novo
      ELSE status::text::status_membro_credencial_novo
    END
  );

DROP TYPE status_membro_credencial;

ALTER TYPE status_membro_credencial_novo RENAME TO status_membro_credencial;