-- ================================================
-- Migration: Drop old security tables
-- Replaced by Casbin (casbin_rule table)
-- ================================================

-- Drop foreign key constraints first
ALTER TABLE auth_usuario DROP CONSTRAINT IF EXISTS fk_auth_usuario_rol;

-- Rename rol_id column to rol_nombre for compatibilidad
ALTER TABLE auth_usuario DROP COLUMN IF EXISTS rol_id;
ALTER TABLE auth_usuario ADD COLUMN IF NOT EXISTS rol_nombre VARCHAR(50);

-- Drop the old tables
DROP TABLE IF EXISTS auth_usuario_permiso CASCADE;
DROP TABLE IF EXISTS rol_permiso CASCADE;
DROP TABLE IF EXISTS permiso CASCADE;
DROP TABLE IF EXISTS rol CASCADE;

-- ================================================
-- Note: Role management is now handled by:
-- - casbin_rule table (policies)
-- - CasbinPolicyController API endpoints
-- ================================================
