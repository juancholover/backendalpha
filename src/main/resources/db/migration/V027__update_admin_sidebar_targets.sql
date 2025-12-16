-- =====================================================
-- V027__update_admin_sidebar_targets.sql
-- Actualizar ADMIN con los 9 sidebar-targets del frontend
-- =====================================================

-- Actualizar isla ADMIN
UPDATE menu_item SET 
    nombre = 'Super Admin',
    descripcion = 'Panel Técnico',
    ruta_frontend = '/super-admin',
    icono = 'shield',
    color = '#10B981'
WHERE codigo = 'ADMIN';

-- Desactivar los sidebar-targets antiguos de ADMIN
UPDATE menu_item SET active = false 
WHERE id_padre = (SELECT id FROM menu_item WHERE codigo = 'ADMIN')
AND codigo IN ('UNIVERSIDADES', 'TIPOS_UNIDAD', 'TIPOS_AUTORIDAD', 'TIPOS_LOCALIZACION', 'UNIDADES_ORGANIZATIVAS', 'PERMISOS');

-- Crear los 9 nuevos sidebar-targets para ADMIN

-- 1. Dashboard (PRINCIPAL)
INSERT INTO menu_item (id_padre, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'ADMIN_DASHBOARD', 'Dashboard', 'Panel principal con métricas del sistema', '/super-admin/dashboard', 'layout-dashboard', 1, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item i WHERE i.codigo = 'ADMIN' AND NOT EXISTS (SELECT 1 FROM menu_item WHERE codigo = 'ADMIN_DASHBOARD');

-- 2. Institución (CONFIGURACIÓN)
INSERT INTO menu_item (id_padre, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'ADMIN_INSTITUCION', 'Institución', 'Gestión de universidades del sistema', '/super-admin/institucion', 'building-2', 2, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item i WHERE i.codigo = 'ADMIN' AND NOT EXISTS (SELECT 1 FROM menu_item WHERE codigo = 'ADMIN_INSTITUCION');

-- 3. Organización
INSERT INTO menu_item (id_padre, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'ADMIN_ORGANIZACION', 'Organización', 'Estructura organizativa y unidades', '/super-admin/organizacion', 'sitemap', 3, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item i WHERE i.codigo = 'ADMIN' AND NOT EXISTS (SELECT 1 FROM menu_item WHERE codigo = 'ADMIN_ORGANIZACION');

-- 4. Usuarios (SEGURIDAD)
INSERT INTO menu_item (id_padre, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'ADMIN_USUARIOS', 'Usuarios', 'Gestión de usuarios del sistema', '/super-admin/usuarios', 'users', 4, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item i WHERE i.codigo = 'ADMIN' AND NOT EXISTS (SELECT 1 FROM menu_item WHERE codigo = 'ADMIN_USUARIOS');

-- 5. Roles y Permisos
INSERT INTO menu_item (id_padre, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'ADMIN_ROLES', 'Roles y Permisos', 'Configuración de roles y permisos', '/super-admin/roles', 'shield-check', 5, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item i WHERE i.codigo = 'ADMIN' AND NOT EXISTS (SELECT 1 FROM menu_item WHERE codigo = 'ADMIN_ROLES');

-- 6. Permisos Individuales
INSERT INTO menu_item (id_padre, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'ADMIN_PERMISOS_IND', 'Permisos Individuales', 'Asignar permisos temporales a usuarios', '/super-admin/permisos-individuales', 'user-check', 6, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item i WHERE i.codigo = 'ADMIN' AND NOT EXISTS (SELECT 1 FROM menu_item WHERE codigo = 'ADMIN_PERMISOS_IND');

-- 7. Auditoría
INSERT INTO menu_item (id_padre, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'ADMIN_AUDITORIA', 'Auditoría', 'Registro de actividades y cambios', '/super-admin/auditoria', 'file-search', 7, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item i WHERE i.codigo = 'ADMIN' AND NOT EXISTS (SELECT 1 FROM menu_item WHERE codigo = 'ADMIN_AUDITORIA');

-- 8. Catálogos (SISTEMA)
INSERT INTO menu_item (id_padre, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'ADMIN_CATALOGOS', 'Catálogos', 'Tablas maestras y configuraciones', '/super-admin/catalogos', 'list', 8, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item i WHERE i.codigo = 'ADMIN' AND NOT EXISTS (SELECT 1 FROM menu_item WHERE codigo = 'ADMIN_CATALOGOS');

-- 9. Backups y Logs
INSERT INTO menu_item (id_padre, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'ADMIN_BACKUPS', 'Backups y Logs', 'Respaldos del sistema y registros', '/super-admin/backups', 'database', 9, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item i WHERE i.codigo = 'ADMIN' AND NOT EXISTS (SELECT 1 FROM menu_item WHERE codigo = 'ADMIN_BACKUPS');

-- Asignar nuevos menús al rol ADMIN
INSERT INTO rol_menu (rol_nombre, menu_item_id, active, created_at, updated_at, created_by)
SELECT 'ADMIN', m.id, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m WHERE m.codigo IN (
    'ADMIN_DASHBOARD', 'ADMIN_INSTITUCION', 'ADMIN_ORGANIZACION', 
    'ADMIN_USUARIOS', 'ADMIN_ROLES', 'ADMIN_PERMISOS_IND', 
    'ADMIN_AUDITORIA', 'ADMIN_CATALOGOS', 'ADMIN_BACKUPS'
)
ON CONFLICT (rol_nombre, menu_item_id) DO NOTHING;

-- =====================================================
-- APIs para cada sidebar-target
-- =====================================================

-- Institución: universidades
INSERT INTO api_permiso (menu_item_id, api_base, descripcion, puede_get, puede_post, puede_put, puede_delete, active, created_at, updated_at, created_by)
SELECT m.id, '/api/v1/universidades', 'Gestión de universidades', true, true, true, true, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m WHERE m.codigo = 'ADMIN_INSTITUCION'
ON CONFLICT (menu_item_id, api_base) DO NOTHING;

-- Organización: unidades organizativas y tipos
INSERT INTO api_permiso (menu_item_id, api_base, descripcion, puede_get, puede_post, puede_put, puede_delete, active, created_at, updated_at, created_by)
SELECT m.id, '/api/v1/unidades-organizativas', 'Unidades organizativas', true, true, true, true, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m WHERE m.codigo = 'ADMIN_ORGANIZACION'
ON CONFLICT (menu_item_id, api_base) DO NOTHING;

INSERT INTO api_permiso (menu_item_id, api_base, descripcion, puede_get, puede_post, puede_put, puede_delete, active, created_at, updated_at, created_by)
SELECT m.id, '/api/v1/tipos-unidad', 'Tipos de unidad', true, true, true, false, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m WHERE m.codigo = 'ADMIN_ORGANIZACION'
ON CONFLICT (menu_item_id, api_base) DO NOTHING;

-- Usuarios: personas y auth
INSERT INTO api_permiso (menu_item_id, api_base, descripcion, puede_get, puede_post, puede_put, puede_delete, active, created_at, updated_at, created_by)
SELECT m.id, '/api/v1/personas', 'Gestión de personas', true, true, true, true, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m WHERE m.codigo = 'ADMIN_USUARIOS'
ON CONFLICT (menu_item_id, api_base) DO NOTHING;

-- Catálogos: tipos
INSERT INTO api_permiso (menu_item_id, api_base, descripcion, puede_get, puede_post, puede_put, puede_delete, active, created_at, updated_at, created_by)
SELECT m.id, '/api/v1/tipos-autoridad', 'Tipos de autoridad', true, true, true, false, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m WHERE m.codigo = 'ADMIN_CATALOGOS'
ON CONFLICT (menu_item_id, api_base) DO NOTHING;

INSERT INTO api_permiso (menu_item_id, api_base, descripcion, puede_get, puede_post, puede_put, puede_delete, active, created_at, updated_at, created_by)
SELECT m.id, '/api/v1/tipos-localizacion', 'Tipos de localización', true, true, true, false, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m WHERE m.codigo = 'ADMIN_CATALOGOS'
ON CONFLICT (menu_item_id, api_base) DO NOTHING;

INSERT INTO api_permiso (menu_item_id, api_base, descripcion, puede_get, puede_post, puede_put, puede_delete, active, created_at, updated_at, created_by)
SELECT m.id, '/api/v1/localizaciones', 'Localizaciones', true, true, true, true, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m WHERE m.codigo = 'ADMIN_CATALOGOS'
ON CONFLICT (menu_item_id, api_base) DO NOTHING;
