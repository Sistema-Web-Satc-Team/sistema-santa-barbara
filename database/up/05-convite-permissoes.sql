
INSERT INTO permissao (nome, criado_em, atualizado_em) VALUES 
('CONVITE:CONVIDAR', NOW(), NOW()),
('CONVITE:LISTAR', NOW(), NOW());


CALL vincular_papel_permissao('SUPER_ADMIN', 'CONVITE:LISTAR');
CALL vincular_papel_permissao('SUPER_ADMIN', 'CONVITE:CONVIDAR');

