INSERT INTO permissao (nome, criado_em, atualizado_em) VALUES 
('PAPEL:ADICIONAR', NOW(), NOW()),
('PAPEL:REMOVER', NOW(), NOW()),
('PAPEL:ATRIBUIR_PERMISSAO', NOW(), NOW()),
('PAPEL:REMOVER_PERMISSAO', NOW(), NOW()),
('PAPEL:VISUALIZAR', NOW(), NOW());

CALL vincular_papel_permissao('SUPER_ADMIN', 'PAPEL:ADICIONAR');
CALL vincular_papel_permissao('SUPER_ADMIN', 'PAPEL:REMOVER');
CALL vincular_papel_permissao('SUPER_ADMIN', 'PAPEL:ATRIBUIR_PERMISSAO');
CALL vincular_papel_permissao('SUPER_ADMIN', 'PAPEL:REMOVER_PERMISSAO');
CALL vincular_papel_permissao('SUPER_ADMIN', 'PAPEL:VISUALIZAR');



INSERT INTO permissao (nome, criado_em, atualizado_em) VALUES
('PERMISSAO:LISTAR', NOW(), NOW());

CALL vincular_papel_permissao('SUPER_ADMIN', 'PERMISSAO:LISTAR');


