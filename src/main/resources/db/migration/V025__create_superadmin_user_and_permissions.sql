-- =====================================================
-- V025__create_superadmin_user_and_permissions.sql
-- Permisos para SuperAdmin (usuario ya existe)
-- =====================================================

-- =====================================================
-- 1. CREAR TIPO DE AUTORIDAD NIVEL 0
-- =====================================================
INSERT INTO tipo_autoridad (nombre, descripcion, nivel_jerarquia, active, created_at, updated_at)
VALUES ('Administrador del Sistema', 'Máxima autoridad del sistema con acceso total', 0, true, NOW(), NOW())
ON CONFLICT (nombre) DO NOTHING;

-- =====================================================
-- 2. ASIGNAR ROL SUPERADMIN EN CASBIN
-- =====================================================
-- Asignación de rol al usuario (ya existe en auth_usuario)
INSERT INTO casbin_rule (ptype, v0, v1) 
VALUES ('g', 'superadmin@upeu.edu.pe', 'SUPERADMIN')
ON CONFLICT DO NOTHING;

-- Políticas de acceso TOTAL para SUPERADMIN
INSERT INTO casbin_rule (ptype, v0, v1, v2) 
VALUES ('p', 'SUPERADMIN', '/api/v1/*', '*')
ON CONFLICT DO NOTHING;


-- =====================================================
-- 5. CREAR ISLA PARA SUPERADMIN
-- =====================================================
INSERT INTO menu_item (codigo, nombre, descripcion, ruta_frontend, icono, color, orden, active, created_at, updated_at, created_by)
VALUES ('SUPERADMIN', 'Administrador del Sistema', 'Panel de control total y configuración avanzada', '/superadmin/dashboard', 'shield-alert', '#7C3AED', 0, true, NOW(), NOW(), 'SYSTEM')
ON CONFLICT (codigo) DO NOTHING;

-- =====================================================
-- 6. CREAR SIDEBAR-TARGETS PARA SUPERADMIN
-- =====================================================

-- Dashboard
INSERT INTO menu_item (id_padre, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'SA_DASHBOARD', 'Dashboard', 'Panel principal con métricas del sistema', '/superadmin/dashboard', 'layout-dashboard', 1, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item i WHERE i.codigo = 'SUPERADMIN' AND NOT EXISTS (SELECT 1 FROM menu_item WHERE codigo = 'SA_DASHBOARD');

-- Institución
INSERT INTO menu_item (id_padre, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'SA_INSTITUCION', 'Institución', 'Gestión de universidades del sistema', '/superadmin/institucion', 'building-2', 2, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item i WHERE i.codigo = 'SUPERADMIN' AND NOT EXISTS (SELECT 1 FROM menu_item WHERE codigo = 'SA_INSTITUCION');

-- Organización
INSERT INTO menu_item (id_padre, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'SA_ORGANIZACION', 'Organización', 'Estructura organizativa y unidades', '/superadmin/organizacion', 'sitemap', 3, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item i WHERE i.codigo = 'SUPERADMIN' AND NOT EXISTS (SELECT 1 FROM menu_item WHERE codigo = 'SA_ORGANIZACION');

-- Usuarios
INSERT INTO menu_item (id_padre, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'SA_USUARIOS', 'Usuarios', 'Gestión de usuarios del sistema', '/superadmin/usuarios', 'users', 4, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item i WHERE i.codigo = 'SUPERADMIN' AND NOT EXISTS (SELECT 1 FROM menu_item WHERE codigo = 'SA_USUARIOS');

-- Roles y Permisos
INSERT INTO menu_item (id_padre, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'SA_ROLES', 'Roles y Permisos', 'Configuración de roles y permisos del sistema', '/superadmin/roles', 'shield-check', 5, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item i WHERE i.codigo = 'SUPERADMIN' AND NOT EXISTS (SELECT 1 FROM menu_item WHERE codigo = 'SA_ROLES');

-- Permisos Individuales
INSERT INTO menu_item (id_padre, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'SA_PERMISOS_IND', 'Permisos Individuales', 'Asignar permisos temporales a usuarios', '/superadmin/permisos-individuales', 'user-check', 6, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item i WHERE i.codigo = 'SUPERADMIN' AND NOT EXISTS (SELECT 1 FROM menu_item WHERE codigo = 'SA_PERMISOS_IND');

-- Auditoría
INSERT INTO menu_item (id_padre, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'SA_AUDITORIA', 'Auditoría', 'Registro de actividades y cambios', '/superadmin/auditoria', 'file-search', 7, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item i WHERE i.codigo = 'SUPERADMIN' AND NOT EXISTS (SELECT 1 FROM menu_item WHERE codigo = 'SA_AUDITORIA');

-- Catálogos
INSERT INTO menu_item (id_padre, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'SA_CATALOGOS', 'Catálogos', 'Tablas maestras y configuraciones', '/superadmin/catalogos', 'list', 8, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item i WHERE i.codigo = 'SUPERADMIN' AND NOT EXISTS (SELECT 1 FROM menu_item WHERE codigo = 'SA_CATALOGOS');

-- Backups y Logs
INSERT INTO menu_item (id_padre, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'SA_BACKUPS', 'Backups y Logs', 'Respaldos del sistema y registros', '/superadmin/backups', 'database', 9, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item i WHERE i.codigo = 'SUPERADMIN' AND NOT EXISTS (SELECT 1 FROM menu_item WHERE codigo = 'SA_BACKUPS');

-- =====================================================
-- 7. ASIGNAR MENÚS AL ROL SUPERADMIN
-- =====================================================

-- Asignar la isla SUPERADMIN
INSERT INTO rol_menu (rol_nombre, menu_item_id, active, created_at, updated_at, created_by)
SELECT 'SUPERADMIN', m.id, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m WHERE m.codigo = 'SUPERADMIN'
ON CONFLICT (rol_nombre, menu_item_id) DO NOTHING;

-- Asignar todos los sidebar-targets de SUPERADMIN
INSERT INTO rol_menu (rol_nombre, menu_item_id, active, created_at, updated_at, created_by)
SELECT 'SUPERADMIN', m.id, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m 
JOIN menu_item isla ON m.id_padre = isla.id
WHERE isla.codigo = 'SUPERADMIN'
ON CONFLICT (rol_nombre, menu_item_id) DO NOTHING;

-- =====================================================
-- 8. CREAR API PERMISOS
-- =====================================================

-- APIs para Institución (universidades)
INSERT INTO api_permiso (menu_item_id, api_base, descripcion, puede_get, puede_post, puede_put, puede_delete, active, created_at, updated_at, created_by)
SELECT m.id, '/api/v1/universidades', 'Gestión de universidades', true, true, true, true, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m WHERE m.codigo = 'SA_INSTITUCION'
ON CONFLICT (menu_item_id, api_base) DO NOTHING;

-- APIs para Organización (unidades organizativas)
INSERT INTO api_permiso (menu_item_id, api_base, descripcion, puede_get, puede_post, puede_put, puede_delete, active, created_at, updated_at, created_by)
SELECT m.id, '/api/v1/unidades-organizativas', 'Unidades organizativas', true, true, true, true, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m WHERE m.codigo = 'SA_ORGANIZACION'
ON CONFLICT (menu_item_id, api_base) DO NOTHING;

INSERT INTO api_permiso (menu_item_id, api_base, descripcion, puede_get, puede_post, puede_put, puede_delete, active, created_at, updated_at, created_by)
SELECT m.id, '/api/v1/tipos-unidad', 'Tipos de unidad organizativa', true, true, true, false, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m WHERE m.codigo = 'SA_ORGANIZACION'
ON CONFLICT (menu_item_id, api_base) DO NOTHING;

-- APIs para Usuarios
INSERT INTO api_permiso (menu_item_id, api_base, descripcion, puede_get, puede_post, puede_put, puede_delete, active, created_at, updated_at, created_by)
SELECT m.id, '/api/v1/personas', 'Gestión de personas', true, true, true, true, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m WHERE m.codigo = 'SA_USUARIOS'
ON CONFLICT (menu_item_id, api_base) DO NOTHING;

INSERT INTO api_permiso (menu_item_id, api_base, descripcion, puede_get, puede_post, puede_put, puede_delete, active, created_at, updated_at, created_by)
SELECT m.id, '/api/v1/auth/usuarios', 'Gestión de usuarios auth', true, true, true, true, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m WHERE m.codigo = 'SA_USUARIOS'
ON CONFLICT (menu_item_id, api_base) DO NOTHING;

-- APIs para Roles y Permisos
INSERT INTO api_permiso (menu_item_id, api_base, descripcion, puede_get, puede_post, puede_put, puede_delete, active, created_at, updated_at, created_by)
SELECT m.id, '/api/v1/menu/islas', 'Gestión de islas', true, true, true, true, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m WHERE m.codigo = 'SA_ROLES'
ON CONFLICT (menu_item_id, api_base) DO NOTHING;

INSERT INTO api_permiso (menu_item_id, api_base, descripcion, puede_get, puede_post, puede_put, puede_delete, active, created_at, updated_at, created_by)
SELECT m.id, '/api/v1/menu/sidebar-targets', 'Gestión de sidebar-targets', true, true, true, true, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m WHERE m.codigo = 'SA_ROLES'
ON CONFLICT (menu_item_id, api_base) DO NOTHING;

INSERT INTO api_permiso (menu_item_id, api_base, descripcion, puede_get, puede_post, puede_put, puede_delete, active, created_at, updated_at, created_by)
SELECT m.id, '/api/v1/menu/apis', 'Gestión de APIs', true, true, true, true, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m WHERE m.codigo = 'SA_ROLES'
ON CONFLICT (menu_item_id, api_base) DO NOTHING;

-- APIs para Catálogos
INSERT INTO api_permiso (menu_item_id, api_base, descripcion, puede_get, puede_post, puede_put, puede_delete, active, created_at, updated_at, created_by)
SELECT m.id, '/api/v1/tipos-autoridad', 'Tipos de autoridad', true, true, true, false, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m WHERE m.codigo = 'SA_CATALOGOS'
ON CONFLICT (menu_item_id, api_base) DO NOTHING;

INSERT INTO api_permiso (menu_item_id, api_base, descripcion, puede_get, puede_post, puede_put, puede_delete, active, created_at, updated_at, created_by)
SELECT m.id, '/api/v1/tipos-localizacion', 'Tipos de localización', true, true, true, false, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m WHERE m.codigo = 'SA_CATALOGOS'
ON CONFLICT (menu_item_id, api_base) DO NOTHING;

INSERT INTO api_permiso (menu_item_id, api_base, descripcion, puede_get, puede_post, puede_put, puede_delete, active, created_at, updated_at, created_by)
SELECT m.id, '/api/v1/localizaciones', 'Localizaciones', true, true, true, true, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m WHERE m.codigo = 'SA_CATALOGOS'
ON CONFLICT (menu_item_id, api_base) DO NOTHING;

-- =====================================================
-- RESUMEN:
-- Usuario: superadmin@upeu.edu.pe
-- Password: SuperAdmin2025!
-- Rol: SUPERADMIN (nivel 0, acceso total)
-- Isla: Administrador del Sistema
-- Sidebar-targets: 9 opciones
-- =====================================================
