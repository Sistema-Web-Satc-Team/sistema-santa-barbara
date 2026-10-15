ALTER TABLE membro_vinculo ADD COLUMN criado_em TIMESTAMPTZ;

UPDATE membro_vinculo
SET criado_em = data_inicio AT TIME ZONE 'UTC';

ALTER TABLE membro_vinculo ALTER COLUMN criado_em SET NOT NULL;

ALTER TABLE membro_vinculo DROP CONSTRAINT ck_membro_vinculo_periodo;
ALTER TABLE membro_vinculo ALTER COLUMN data_inicio DROP DEFAULT;

ALTER TABLE membro_vinculo
    ALTER COLUMN data_inicio TYPE DATE
    USING ((data_inicio AT TIME ZONE 'UTC') AT TIME ZONE 'America/Sao_Paulo')::date;

ALTER TABLE membro_vinculo
    ALTER COLUMN data_termino TYPE DATE
    USING ((data_termino AT TIME ZONE 'UTC') AT TIME ZONE 'America/Sao_Paulo')::date;

ALTER TABLE membro_vinculo
    ADD CONSTRAINT ck_membro_vinculo_periodo
    CHECK (data_termino IS NULL OR data_termino >= data_inicio);