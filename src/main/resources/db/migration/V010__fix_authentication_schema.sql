-- V010: Fix authentication schema inconsistencies
-- This migration fixes issues discovered during authentication implementation:
-- 1. Rename persona.nombre to persona.nombres (if exists)
-- 2. Update super admin password hash to valid BCrypt
-- 3. Recreate refresh_tokens table with correct column names

-- =============================================================================
-- 1. Fix persona table column name (idempotent)
-- =============================================================================
DO $$
BEGIN
    -- Check if column 'nombre' exists and rename it to 'nombres'
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'persona' 
        AND column_name = 'nombre'
    ) THEN
        ALTER TABLE persona RENAME COLUMN nombre TO nombres;
        RAISE NOTICE 'Column persona.nombre renamed to persona.nombres';
    ELSE
        RAISE NOTICE 'Column persona.nombres already exists, skipping rename';
    END IF;
END $$;

-- =============================================================================
-- 2. Update super admin password hash to valid BCrypt
-- =============================================================================
-- Password: SuperAdmin2025!
-- BCrypt hash generated with at.favre.lib:bcrypt:0.10.2 (cost factor 10)
-- Force update to ensure correct hash (idempotent - always sets same value)
UPDATE auth_usuario 
SET password_hash = '$2a$10$4CXnmWn5Z9bquEI5qW3k8O/mOHIyutLauNACSfiuPv2pBJbHRqWVG',
    updated_at = NOW(),
    updated_by = 'V010_migration'
WHERE id = 1;

-- =============================================================================
-- 3. Recreate refresh_tokens table with correct schema
-- =============================================================================
-- Drop existing table (cascade to remove dependencies)
DROP TABLE IF EXISTS refresh_tokens CASCADE;

-- Create table with column names matching RefreshToken entity
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    auth_usuario_id BIGINT NOT NULL,
    token VARCHAR(1000) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,              -- Changed from fecha_expiracion
    is_revoked BOOLEAN DEFAULT false,           -- Changed from revocado
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_refresh_token_auth_usuario 
        FOREIGN KEY (auth_usuario_id) 
        REFERENCES auth_usuario(id) 
        ON DELETE CASCADE
);

-- Create indexes for performance
CREATE INDEX idx_refresh_token_auth_usuario ON refresh_tokens(auth_usuario_id);
CREATE INDEX idx_refresh_token_expires_at ON refresh_tokens(expires_at);
CREATE INDEX idx_refresh_token_active ON refresh_tokens(active);

-- Comments for documentation
COMMENT ON TABLE refresh_tokens IS 'JWT refresh tokens for user authentication';
COMMENT ON COLUMN refresh_tokens.token IS 'JWT refresh token string (max 1000 chars)';
COMMENT ON COLUMN refresh_tokens.expires_at IS 'Token expiration timestamp';
COMMENT ON COLUMN refresh_tokens.is_revoked IS 'Whether token has been manually revoked';
