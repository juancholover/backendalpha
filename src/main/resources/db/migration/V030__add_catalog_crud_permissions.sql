-- V030__add_catalog_crud_permissions.sql
-- Agregar permisos CRUD para tablas de catálogo

-- Permisos para tipos-unidad (POST, PUT, DELETE)
INSERT INTO casbin_rule (ptype, v0, v1, v2)
VALUES 
    -- SUPERADMIN
    ('p', 'SUPERADMIN', '/api/v1/tipos-unidad', 'POST'),
    ('p', 'SUPERADMIN', '/api/v1/tipos-unidad/*', 'PUT'),
    ('p', 'SUPERADMIN', '/api/v1/tipos-unidad/*', 'DELETE'),
    
    -- ADMIN
    ('p', 'ADMIN', '/api/v1/tipos-unidad', 'POST'),
    ('p', 'ADMIN', '/api/v1/tipos-unidad/*', 'PUT'),
    ('p', 'ADMIN', '/api/v1/tipos-unidad/*', 'DELETE')
ON CONFLICT DO NOTHING;

-- Permisos para tipos-localizacion (POST, PUT, DELETE)
INSERT INTO casbin_rule (ptype, v0, v1, v2)
VALUES 
    -- SUPERADMIN
    ('p', 'SUPERADMIN', '/api/v1/tipos-localizacion', 'POST'),
    ('p', 'SUPERADMIN', '/api/v1/tipos-localizacion/*', 'PUT'),
    ('p', 'SUPERADMIN', '/api/v1/tipos-localizacion/*', 'DELETE'),
    
    -- ADMIN
    ('p', 'ADMIN', '/api/v1/tipos-localizacion', 'POST'),
    ('p', 'ADMIN', '/api/v1/tipos-localizacion/*', 'PUT'),
    ('p', 'ADMIN', '/api/v1/tipos-localizacion/*', 'DELETE')
ON CONFLICT DO NOTHING;

-- Permisos para tipos-autoridad
INSERT INTO casbin_rule (ptype, v0, v1, v2)
VALUES 
    ('p', 'SUPERADMIN', '/api/v1/tipos-autoridad', 'POST'),
    ('p', 'SUPERADMIN', '/api/v1/tipos-autoridad/*', 'PUT'),
    ('p', 'SUPERADMIN', '/api/v1/tipos-autoridad/*', 'DELETE'),
    ('p', 'ADMIN', '/api/v1/tipos-autoridad', 'POST'),
    ('p', 'ADMIN', '/api/v1/tipos-autoridad/*', 'PUT'),
    ('p', 'ADMIN', '/api/v1/tipos-autoridad/*', 'DELETE')
ON CONFLICT DO NOTHING;

-- Permisos para universidades (target: institucion)
INSERT INTO casbin_rule (ptype, v0, v1, v2)
VALUES 
    ('p', 'SUPERADMIN', '/api/v1/universidades', 'POST'),
    ('p', 'SUPERADMIN', '/api/v1/universidades/*', 'PUT'),
    ('p', 'SUPERADMIN', '/api/v1/universidades/*', 'DELETE'),
    ('p', 'ADMIN', '/api/v1/universidades', 'POST'),
    ('p', 'ADMIN', '/api/v1/universidades/*', 'PUT'),
    ('p', 'ADMIN', '/api/v1/universidades/*', 'DELETE')
ON CONFLICT DO NOTHING;

-- Permisos para localizaciones
INSERT INTO casbin_rule (ptype, v0, v1, v2)
VALUES 
    ('p', 'SUPERADMIN', '/api/v1/localizaciones', 'POST'),
    ('p', 'SUPERADMIN', '/api/v1/localizaciones/*', 'PUT'),
    ('p', 'SUPERADMIN', '/api/v1/localizaciones/*', 'DELETE'),
    ('p', 'ADMIN', '/api/v1/localizaciones', 'POST'),
    ('p', 'ADMIN', '/api/v1/localizaciones/*', 'PUT'),
    ('p', 'ADMIN', '/api/v1/localizaciones/*', 'DELETE')
ON CONFLICT DO NOTHING;

-- Permisos para unidades-organizativas
INSERT INTO casbin_rule (ptype, v0, v1, v2)
VALUES 
    ('p', 'SUPERADMIN', '/api/v1/unidades-organizativas', 'POST'),
    ('p', 'SUPERADMIN', '/api/v1/unidades-organizativas/*', 'PUT'),
    ('p', 'SUPERADMIN', '/api/v1/unidades-organizativas/*', 'DELETE'),
    ('p', 'ADMIN', '/api/v1/unidades-organizativas', 'POST'),
    ('p', 'ADMIN', '/api/v1/unidades-organizativas/*', 'PUT'),
    ('p', 'ADMIN', '/api/v1/unidades-organizativas/*', 'DELETE')
ON CONFLICT DO NOTHING;

-- Permisos para autoridades
INSERT INTO casbin_rule (ptype, v0, v1, v2)
VALUES 
    ('p', 'SUPERADMIN', '/api/v1/autoridades', 'POST'),
    ('p', 'SUPERADMIN', '/api/v1/autoridades/*', 'PUT'),
    ('p', 'SUPERADMIN', '/api/v1/autoridades/*', 'DELETE'),
    ('p', 'ADMIN', '/api/v1/autoridades', 'POST'),
    ('p', 'ADMIN', '/api/v1/autoridades/*', 'PUT'),
    ('p', 'ADMIN', '/api/v1/autoridades/*', 'DELETE')
ON CONFLICT DO NOTHING;

-- Permisos para personas
INSERT INTO casbin_rule (ptype, v0, v1, v2)
VALUES 
    ('p', 'SUPERADMIN', '/api/v1/personas', 'POST'),
    ('p', 'SUPERADMIN', '/api/v1/personas/*', 'PUT'),
    ('p', 'SUPERADMIN', '/api/v1/personas/*', 'DELETE'),
    ('p', 'ADMIN', '/api/v1/personas', 'POST'),
    ('p', 'ADMIN', '/api/v1/personas/*', 'PUT'),
    ('p', 'ADMIN', '/api/v1/personas/*', 'DELETE')
ON CONFLICT DO NOTHING;

-- Log
DO $$
BEGIN
    RAISE NOTICE 'Permisos CRUD agregados para tablas de catálogo e institución';
END $$;
