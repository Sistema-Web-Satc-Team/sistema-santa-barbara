
-- V022__seed_demo_data.sql
-- Seed de demonstração do Sistema Santa Bárbara.
-- Executar após as migrations V001-V021.
-- Não cria credenciais de acesso.
-- Não cria vínculos ADMIN ou SUPER_ADMIN.

BEGIN;

-- =========================================================
-- 1. VALIDAR FUNÇÕES NECESSÁRIAS
-- =========================================================

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM funcao WHERE code = 'SECRETARIO')
       OR NOT EXISTS (SELECT 1 FROM funcao WHERE code = 'ALUNO')
       OR NOT EXISTS (SELECT 1 FROM funcao WHERE code = 'PROFESSOR')
       OR NOT EXISTS (SELECT 1 FROM funcao WHERE code = 'MUSICO_BANDA') THEN
        RAISE EXCEPTION 'Uma ou mais funções necessárias não existem.';
    END IF;
END $$;


-- =========================================================
-- 2. CRIAR 50 MEMBROS
-- =========================================================

INSERT INTO membro (
    id,
    nome_legal,
    data_nascimento,
    sexo,
    endereco,
    telefone,
    email,
    criado_em,
    atualizado_em
)
SELECT
    md5('demo-membro-' || lpad(n::text, 2, '0'))::uuid,

    (ARRAY[
        'Ana Almeida',
        'Bruno Barbosa',
        'Camila Cardoso',
        'Daniel Dias',
        'Eduarda Esteves',
        'Felipe Ferreira',
        'Gabriela Gomes',
        'Henrique Henrique',
        'Isabela Ibrahim',
        'João Junqueira',
        'Karen Klein',
        'Lucas Lopes',
        'Mariana Martins',
        'Nicolas Nogueira',
        'Olivia Oliveira',
        'Pedro Pereira',
        'Rafaela Queiroz',
        'Samuel Ribeiro',
        'Talita Santos',
        'Victor Teixeira',
        'Amanda Uchoa',
        'Caio Vieira',
        'Débora Wagner',
        'Enzo Xavier',
        'Fernanda Yamamoto',
        'Gustavo Zanetti',
        'Helena Costa',
        'Igor Moreira',
        'Julia Souza',
        'Kevin Mendes',
        'Larissa Carvalho',
        'Matheus Rocha',
        'Natália Pinto',
        'Otávio Moura',
        'Paula Freitas',
        'Rafael Azevedo',
        'Sofia Cunha',
        'Thiago Monteiro',
        'Valentina Machado',
        'William Farias',
        'Yasmin Moraes',
        'André Ramos',
        'Bianca Batista',
        'César Correia',
        'Daniela Campos',
        'Elias Andrade',
        'Flávia Peixoto',
        'Murilo Barros',
        'Priscila Rezende',
        'Vinícius Duarte'
    ]::text[])[n],

    make_date(
        1985 + (n % 22),
        1 + ((n * 5) % 12),
        1 + ((n * 7) % 27)
    ),

    CASE
        WHEN n % 2 = 0 THEN 'MASCULINO'
        ELSE 'FEMININO'
    END,

    'Rua das Acácias, ' || (100 + n) || ', Centro',

    '4899' || lpad(n::text, 7, '0'),

    'demo' || lpad(n::text, 3, '0') || '@santabarbara.local',

    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP

FROM generate_series(1, 50) AS n;


-- =========================================================
-- 3. ESPECIALIZAÇÕES DE ALUNO E PROFESSOR
-- =========================================================

-- 34 alunos: membros 2 a 30 e 46 a 50.

INSERT INTO aluno (id_membro)
SELECT md5('demo-membro-' || lpad(n::text, 2, '0'))::uuid
FROM (
    SELECT generate_series(2, 30) AS n
    UNION ALL
    SELECT generate_series(46, 50)
) AS alunos;


-- 5 professores: membros 31 a 35.

INSERT INTO professor (id_membro)
SELECT md5('demo-membro-' || lpad(n::text, 2, '0'))::uuid
FROM generate_series(31, 35) AS n;


-- =========================================================
-- 4. VÍNCULOS INSTITUCIONAIS
-- =========================================================

-- Distribuição:
-- 1 SECRETARIO
-- 34 ALUNO
-- 5 PROFESSOR
-- 15 MUSICO_BANDA
--
-- Os membros 46 a 50 possuem ALUNO e MUSICO_BANDA.

INSERT INTO membro_vinculo (
    id_funcao,
    id_membro,
    data_inicio
)
SELECT
    f.id,
    md5('demo-membro-' || lpad(v.n::text, 2, '0'))::uuid,
    TIMESTAMP '2026-07-20 08:00:00'
FROM (
    SELECT 1 AS n, 'SECRETARIO' AS funcao

    UNION ALL

    SELECT n, 'ALUNO'
    FROM generate_series(2, 30) AS n

    UNION ALL

    SELECT n, 'ALUNO'
    FROM generate_series(46, 50) AS n

    UNION ALL

    SELECT n, 'PROFESSOR'
    FROM generate_series(31, 35) AS n

    UNION ALL

    SELECT n, 'MUSICO_BANDA'
    FROM generate_series(36, 50) AS n

) AS v
JOIN funcao f ON f.code = v.funcao;


-- =========================================================
-- 5. SALAS
-- =========================================================

INSERT INTO sala (id, code, capacidade)
OVERRIDING SYSTEM VALUE
VALUES
    (9001, 'SALA-DEMO-01', 12),
    (9002, 'SALA-DEMO-02', 15),
    (9003, 'SALA-DEMO-03', 18),
    (9004, 'SALA-DEMO-04', 12),
    (9005, 'SALA-DEMO-05', 20);


-- =========================================================
-- 6. INSTRUMENTOS
-- =========================================================

INSERT INTO instrumento (
    id,
    nome,
    criado_em,
    atualizado_em,
    descricao,
    tipo
)
OVERRIDING SYSTEM VALUE
VALUES
    (
        9101,
        'Violão',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        'Instrumento de cordas dedilhadas',
        'CORDAS'
    ),
    (
        9102,
        'Flauta transversal',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        'Instrumento de sopro',
        'SOPRO'
    ),
    (
        9103,
        'Clarinete',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        'Instrumento de sopro',
        'SOPRO'
    ),
    (
        9104,
        'Trompete',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        'Instrumento de sopro',
        'SOPRO'
    ),
    (
        9105,
        'Teclado',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        'Instrumento de teclas',
        'TECLAS'
    );


-- =========================================================
-- 7. HORÁRIOS
-- =========================================================

INSERT INTO horario (id)
OVERRIDING SYSTEM VALUE
SELECT generate_series(9201, 9210);


-- =========================================================
-- 8. INTERVALOS DOS HORÁRIOS
-- =========================================================

INSERT INTO horario_intervalo (
    id,
    id_horario,
    dia_semana,
    inicio,
    fim
)
OVERRIDING SYSTEM VALUE
VALUES
    (9301, 9201, 'SEGUNDA', TIME '08:00', TIME '09:00'),
    (9302, 9202, 'SEGUNDA', TIME '09:00', TIME '10:00'),
    (9303, 9203, 'SEGUNDA', TIME '10:00', TIME '11:00'),

    (9304, 9204, 'TERCA', TIME '08:00', TIME '09:00'),
    (9305, 9205, 'TERCA', TIME '09:00', TIME '10:00'),

    (9306, 9206, 'QUARTA', TIME '08:00', TIME '09:00'),
    (9307, 9207, 'QUARTA', TIME '09:00', TIME '10:00'),

    (9308, 9208, 'QUINTA', TIME '10:00', TIME '11:00'),
    (9309, 9209, 'QUINTA', TIME '14:00', TIME '15:00'),

    (9310, 9210, 'SEXTA', TIME '15:00', TIME '16:00');


-- =========================================================
-- 9. TURMAS
-- =========================================================

INSERT INTO turma (
    id,
    nome,
    sala_id,
    horario_id,
    data_inicio,
    data_fim,
    data_termino,
    status,
    numero_vagas,
    turno
)
OVERRIDING SYSTEM VALUE
VALUES
    (
        9401, 'Violão I - Iniciante',
        9001, 9201,
        DATE '2026-08-03', DATE '2026-12-18',
        NULL, 'ATIVA', 10, 'MATUTINO'
    ),
    (
        9402, 'Flauta I - Iniciante',
        9002, 9202,
        DATE '2026-08-03', DATE '2026-12-18',
        NULL, 'ATIVA', 10, 'MATUTINO'
    ),
    (
        9403, 'Clarinete I - Iniciante',
        9003, 9203,
        DATE '2026-08-03', DATE '2026-12-18',
        NULL, 'ATIVA', 10, 'MATUTINO'
    ),
    (
        9404, 'Trompete I - Iniciante',
        9004, 9204,
        DATE '2026-08-03', DATE '2026-12-18',
        NULL, 'ATIVA', 10, 'MATUTINO'
    ),
    (
        9405, 'Teclado I - Iniciante',
        9005, 9205,
        DATE '2026-08-03', DATE '2026-12-18',
        NULL, 'ATIVA', 10, 'MATUTINO'
    ),
    (
        9406, 'Violão II - Prática',
        9001, 9206,
        DATE '2026-08-03', DATE '2026-12-18',
        NULL, 'ATIVA', 10, 'MATUTINO'
    ),
    (
        9407, 'Flauta II - Prática',
        9002, 9207,
        DATE '2026-08-03', DATE '2026-12-18',
        NULL, 'ATIVA', 10, 'MATUTINO'
    ),
    (
        9408, 'Clarinete II - Prática',
        9003, 9208,
        DATE '2026-08-03', DATE '2026-12-18',
        NULL, 'ATIVA', 10, 'MATUTINO'
    ),
    (
        9409, 'Trompete II - Prática',
        9004, 9209,
        DATE '2026-08-03', DATE '2026-12-18',
        NULL, 'ATIVA', 10, 'MATUTINO'
    ),
    (
        9410, 'Teclado II - Prática',
        9005, 9210,
        DATE '2026-08-03', DATE '2026-12-18',
        NULL, 'ATIVA', 10, 'MATUTINO'
    );


-- =========================================================
-- 10. INSTRUMENTOS DAS TURMAS
-- =========================================================

INSERT INTO instrumento_turma (
    id_turma,
    id_instrumento
)
VALUES
    (9401, 9101),
    (9402, 9102),
    (9403, 9103),
    (9404, 9104),
    (9405, 9105),
    (9406, 9101),
    (9407, 9102),
    (9408, 9103),
    (9409, 9104),
    (9410, 9105);


-- =========================================================
-- 11. PROFESSORES DAS TURMAS
-- =========================================================

-- Cada professor assume duas turmas.
-- As turmas de cada professor acontecem em dias diferentes.

INSERT INTO professor_turma (
    id_turma,
    id_professor,
    status,
    data_entrada
)
VALUES
    (9401, md5('demo-membro-31')::uuid, 'ATIVO', DATE '2026-07-20'),
    (9402, md5('demo-membro-32')::uuid, 'ATIVO', DATE '2026-07-20'),
    (9403, md5('demo-membro-33')::uuid, 'ATIVO', DATE '2026-07-20'),
    (9404, md5('demo-membro-34')::uuid, 'ATIVO', DATE '2026-07-20'),
    (9405, md5('demo-membro-35')::uuid, 'ATIVO', DATE '2026-07-20'),

    (9406, md5('demo-membro-31')::uuid, 'ATIVO', DATE '2026-07-20'),
    (9407, md5('demo-membro-32')::uuid, 'ATIVO', DATE '2026-07-20'),
    (9408, md5('demo-membro-33')::uuid, 'ATIVO', DATE '2026-07-20'),
    (9409, md5('demo-membro-34')::uuid, 'ATIVO', DATE '2026-07-20'),
    (9410, md5('demo-membro-35')::uuid, 'ATIVO', DATE '2026-07-20');


-- =========================================================
-- 12. MATRÍCULAS DOS ALUNOS
-- =========================================================

-- 30 matrículas ativas.
-- Três alunos diferentes em cada turma.
-- Membros 2 a 30 e membro 46.

INSERT INTO matricula_aluno_turma (
    id_turma,
    id_aluno,
    status,
    data_matricula
)
SELECT
    9401 + ((posicao - 1) / 3),
    md5(
        'demo-membro-' ||
        lpad(
            CASE
                WHEN posicao <= 29 THEN (posicao + 1)::text
                ELSE '46'
            END,
            2,
            '0'
        )
    )::uuid,
    'ATIVO',
    DATE '2026-07-25'
FROM generate_series(1, 30) AS posicao;


-- =========================================================
-- 13. ATUALIZAR SEQUÊNCIAS IDENTITY
-- =========================================================

SELECT setval(
    pg_get_serial_sequence('sala', 'id'),
    (SELECT MAX(id) FROM sala),
    true
);

SELECT setval(
    pg_get_serial_sequence('instrumento', 'id'),
    (SELECT MAX(id) FROM instrumento),
    true
);

SELECT setval(
    pg_get_serial_sequence('horario', 'id'),
    (SELECT MAX(id) FROM horario),
    true
);

SELECT setval(
    pg_get_serial_sequence('horario_intervalo', 'id'),
    (SELECT MAX(id) FROM horario_intervalo),
    true
);

SELECT setval(
    pg_get_serial_sequence('turma', 'id'),
    (SELECT MAX(id) FROM turma),
    true
);

COMMIT;
