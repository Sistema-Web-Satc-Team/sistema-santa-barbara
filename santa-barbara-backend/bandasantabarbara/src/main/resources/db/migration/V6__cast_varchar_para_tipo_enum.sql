ALTER TYPE tipo_sexo_pessoa RENAME VALUE 'NAO INFORMADO' TO 'NAO_INFORMADO';

CREATE CAST (varchar AS tipo_sexo_pessoa)          WITH INOUT AS IMPLICIT;
CREATE CAST (varchar AS status_membro_credencial)  WITH INOUT AS IMPLICIT;
CREATE CAST (varchar AS tipo_funcao)               WITH INOUT AS IMPLICIT;
CREATE CAST (varchar AS tipo_relacao_responsavel)  WITH INOUT AS IMPLICIT;
CREATE CAST (varchar AS tipo_vinculo_instrumento)  WITH INOUT AS IMPLICIT;
CREATE CAST (varchar AS status_convite)            WITH INOUT AS IMPLICIT;