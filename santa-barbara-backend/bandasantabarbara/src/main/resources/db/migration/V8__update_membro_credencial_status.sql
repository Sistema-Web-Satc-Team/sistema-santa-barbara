-- 1. Novo tipo
CREATE TYPE status_membro_credencial_novo AS ENUM ('ATIVO', 'SUSPENSO', 'BLOQUEADO');

-- 2. Remove o default, se existir (referencia o tipo antigo)
ALTER TABLE membro_credencial ALTER COLUMN status DROP DEFAULT;

-- 3. Converte a coluna
ALTER TABLE membro_credencial
  ALTER COLUMN status TYPE status_membro_credencial_novo
  USING (
    CASE status::text
      WHEN 'ATIVO'     THEN 'ATIVO'
      WHEN 'BLOQUEADO' THEN 'BLOQUEADO'
      ELSE 'SUSPENSO'
    END::status_membro_credencial_novo
  );

-- 4. Recria o default, se existia
ALTER TABLE membro_credencial
  ALTER COLUMN status SET DEFAULT 'ATIVO'::status_membro_credencial_novo;

-- 5. Remove o cast antigo e o tipo antigo
DROP CAST (character varying AS status_membro_credencial);
DROP TYPE status_membro_credencial;

-- 6. Renomeia o tipo novo
ALTER TYPE status_membro_credencial_novo RENAME TO status_membro_credencial;

-- 7. Recria o cast para o tipo novo
CREATE CAST (character varying AS status_membro_credencial) WITH INOUT AS IMPLICIT;