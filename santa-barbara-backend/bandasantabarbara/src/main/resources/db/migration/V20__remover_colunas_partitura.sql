-- ============================================================================
-- MIGRATION V20: Remoção de campos que não serão usadas ainda na partitura
-- ============================================================================

ALTER TABLE partitura
    DROP COLUMN IF EXISTS dificuldade,
    DROP COLUMN IF EXISTS tipo_material_performance,
    DROP COLUMN IF EXISTS deletado_em;
