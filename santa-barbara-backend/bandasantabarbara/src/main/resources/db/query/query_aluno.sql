SELECT
	a.id_membro, 
	m.nome_legal, 
	m.data_nascimento, 
	m.sexo, 
	m.endereco, 
	m.telefone, 
	m.email, 
	mv.data_inicio, 
	mv.data_termino, 
	f.code,
	f.nome

FROM aluno a
JOIN membro m ON m.id = a.id_membro
JOIN membro_vinculo mv ON mv.id_membro = m.id
JOIN funcao f ON f.id = mv.id_funcao
WHERE f.code LIKE 'ALUNO';

-- =======================
-- Separar no Repository
-- =======================

-- Verifica se ele esta em uma turma

SELECT
	a.id_membro,
	EXISTS (
        SELECT 1
        FROM matricula_aluno_turma mat
        WHERE mat.id_aluno = a.id_membro
          AND mat.status = 'ATIVO'
    ) AS tem_turma
FROM aluno a;

-- Pega as turmas do aluno

SELECT DISTINCT
    mat.id_aluno,
    i.id,
    i.nome
FROM matricula_aluno_turma mat
JOIN turma t
    ON t.id = mat.id_turma
JOIN instrumento_turma it
    ON it.id_turma = t.id
JOIN instrumento i
    ON i.id = it.id_instrumento
WHERE mat.status = 'ATIVO';

-- Pega os responsaveis do aluno

SELECT
    mr.id_membro_dependente AS id_aluno,
    responsavel.id,
    responsavel.nome_legal,
    responsavel.telefone,
    responsavel.email,
    mr.tipo_relacao
FROM membro_responsavel mr
JOIN membro responsavel
    ON responsavel.id = mr.id_membro_responsavel;