
ALTER TABLE membro_vinculo DROP CONSTRAINT pk_membro_vinculo;


ALTER TABLE membro_vinculo
    ADD CONSTRAINT pk_membro_vinculo PRIMARY KEY (id, id_membro);