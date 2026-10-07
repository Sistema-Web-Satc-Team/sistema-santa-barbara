
-- Pendente foi removido pois conceitualmente não fazia sentido;
-- Se o canal de email não enviou significa que teve falha no envio;

-- Relação: Um membro recebe vários convites, mas um convite pertence a um só membro

CREATE TYPE status_convite AS ENUM (
    'ENVIADO',
    'REENVIADO',
    'ACEITO',
    'EXPIRADO',
    'FALHA_ENVIO',
    'FALHA_REENVIO'
);

CREATE TABLE IF NOT EXISTS convite (
    id UUID,
    id_membro UUID,
    data_convite_criacao DATE NOT NULL DEFAULT CURRENT_DATE,
    data_convite_expiracao DATE NULL, -- Responsabilidade do backend e dominio da aplicação.
    status status_convite NOT NULL,

    CONSTRAINT fk_convite_membro FOREIGN KEY (id_membro) REFERENCES membro(id),
    CONSTRAINT pk_convite PRIMARY KEY (id, id_membro)
);