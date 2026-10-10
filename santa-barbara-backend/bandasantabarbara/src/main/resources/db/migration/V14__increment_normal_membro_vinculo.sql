ALTER TABLE membro_vinculo DROP CONSTRAINT pk_membro_vinculo;

UPDATE membro_vinculo mv
SET id = sub.rn
FROM (
    SELECT ctid AS linha, row_number() OVER (ORDER BY criado_em, id_membro) AS rn
    FROM membro_vinculo
) sub
WHERE mv.ctid = sub.linha;

ALTER TABLE membro_vinculo ADD CONSTRAINT pk_membro_vinculo PRIMARY KEY (id);

SELECT setval(pg_get_serial_sequence('membro_vinculo', 'id'),
              COALESCE((SELECT MAX(id) FROM membro_vinculo), 1));



CREATE INDEX idx_membro_vinculo_id_membro ON membro_vinculo (id_membro);