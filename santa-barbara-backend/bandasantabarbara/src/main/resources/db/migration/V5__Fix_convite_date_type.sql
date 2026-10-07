ALTER TABLE convite
    ALTER COLUMN data_convite_criacao TYPE TIMESTAMP WITH TIME ZONE USING data_convite_criacao AT TIME ZONE 'UTC',
    ALTER COLUMN data_convite_expiracao TYPE TIMESTAMP WITH TIME ZONE USING data_convite_expiracao AT TIME ZONE 'UTC';