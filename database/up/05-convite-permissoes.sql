
INSERT INTO permissao (nome, criado_em, atualizado_em) VALUES 
('CONVITE:CONVIDAR', NOW(), NOW()),
('CONVITE:LISTAR', NOW(), NOW()),
('CONVITE:VISUALIZAR', NOW(), NOW());


CALL vincular_papel_permissao('SUPER_ADMIN', 'CONVITE:LISTAR');
CALL vincular_papel_permissao('SUPER_ADMIN', 'CONVITE:CONVIDAR');
CALL vincular_papel_permissao('SUPER_ADMIN', 'CONVITE:VISUALIZAR');

