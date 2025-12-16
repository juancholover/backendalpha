-- V029__add_upload_permissions.sql
-- Agregar permisos para endpoints de upload bajo /universidades

-- Permisos para uploads de universidades (solo 4 columnas: ptype, v0, v1, v2)
INSERT INTO casbin_rule (ptype, v0, v1, v2)
VALUES 
    -- Upload logo de universidad
    ('p', 'SUPERADMIN', '/api/v1/universidades/*/logo', 'PUT'),
    ('p', 'ADMIN', '/api/v1/universidades/*/logo', 'PUT'),
    
    -- Upload header logo
    ('p', 'SUPERADMIN', '/api/v1/universidades/*/landing/header/logo', 'PUT'),
    ('p', 'ADMIN', '/api/v1/universidades/*/landing/header/logo', 'PUT'),
    
    -- Upload hero video
    ('p', 'SUPERADMIN', '/api/v1/universidades/*/landing/hero/video', 'PUT'),
    ('p', 'ADMIN', '/api/v1/universidades/*/landing/hero/video', 'PUT'),
    
    -- Upload hero slides
    ('p', 'SUPERADMIN', '/api/v1/universidades/*/landing/hero/slide/*', 'PUT'),
    ('p', 'ADMIN', '/api/v1/universidades/*/landing/hero/slide/*', 'PUT'),
    
    -- Upload sede images
    ('p', 'SUPERADMIN', '/api/v1/universidades/*/landing/sede/*', 'PUT'),
    ('p', 'ADMIN', '/api/v1/universidades/*/landing/sede/*', 'PUT'),
    
    -- Upload config images
    ('p', 'SUPERADMIN', '/api/v1/universidades/*/configuracion/*/imagen', 'PUT'),
    ('p', 'ADMIN', '/api/v1/universidades/*/configuracion/*/imagen', 'PUT')
ON CONFLICT DO NOTHING;

-- Log
DO $$
BEGIN
    RAISE NOTICE 'Permisos de upload agregados para SUPERADMIN y ADMIN';
END $$;
