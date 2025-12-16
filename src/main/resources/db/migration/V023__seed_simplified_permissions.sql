-- =====================================================
-- V023__seed_simplified_permissions.sql
-- Datos iniciales para el sistema simplificado de permisos
-- =====================================================

-- =====================================================
-- 1. ISLAS (menu_item con id_padre = NULL)
-- =====================================================
INSERT INTO menu_item (codigo, nombre, descripcion, ruta_frontend, icono, color, orden, active, created_at, updated_at, created_by)
VALUES 
    ('ADMIN', 'Administrador', 'Panel de administración del sistema', '/admin/dashboard', 'shield-check', '#DC2626', 1, true, NOW(), NOW(), 'SYSTEM'),
    ('RECTOR', 'Rector', 'Gestión y supervisión universitaria', '/rector/dashboard', 'crown', '#8B0000', 2, true, NOW(), NOW(), 'SYSTEM'),
    ('DECANO', 'Decano', 'Gestión de facultad', '/decano/dashboard', 'building-2', '#1E40AF', 3, true, NOW(), NOW(), 'SYSTEM'),
    ('PROFESOR', 'Profesor', 'Gestión académica docente', '/profesor/dashboard', 'graduation-cap', '#059669', 4, true, NOW(), NOW(), 'SYSTEM'),
    ('ESTUDIANTE', 'Estudiante', 'Portal del estudiante', '/estudiante/dashboard', 'user', '#7C3AED', 5, true, NOW(), NOW(), 'SYSTEM')
ON CONFLICT (codigo) DO NOTHING;

-- =====================================================
-- 2. SIDEBAR-TARGETS PARA ADMIN
-- =====================================================
INSERT INTO menu_item (id_padre, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'UNIVERSIDADES', 'Universidades', 'Gestión de universidades', '/admin/universidades', 'building', 1, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item i WHERE i.codigo = 'ADMIN'
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO menu_item (id_padre, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'TIPOS_UNIDAD', 'Tipos de Unidad', 'Catálogo de tipos de unidad', '/admin/tipos-unidad', 'tags', 2, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item i WHERE i.codigo = 'ADMIN'
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO menu_item (id_padre, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'TIPOS_AUTORIDAD', 'Tipos de Autoridad', 'Catálogo de tipos de autoridad', '/admin/tipos-autoridad', 'crown', 3, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item i WHERE i.codigo = 'ADMIN'
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO menu_item (id_padre, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'TIPOS_LOCALIZACION', 'Tipos de Localización', 'Catálogo de localización', '/admin/tipos-localizacion', 'map-pin', 4, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item i WHERE i.codigo = 'ADMIN'
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO menu_item (id_padre, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'UNIDADES_ORGANIZATIVAS', 'Unidades Organizativas', 'Estructura organizativa', '/admin/unidades-organizativas', 'sitemap', 5, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item i WHERE i.codigo = 'ADMIN'
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO menu_item (id_padre, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'PERMISOS', 'Permisos', 'Gestión de roles y permisos', '/admin/permisos', 'lock', 6, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item i WHERE i.codigo = 'ADMIN'
ON CONFLICT (codigo) DO NOTHING;

-- =====================================================
-- 3. SIDEBAR-TARGETS PARA PROFESOR
-- =====================================================
INSERT INTO menu_item (id_padre, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'MIS_CURSOS', 'Mis Cursos', 'Cursos asignados', '/profesor/cursos', 'book', 1, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item i WHERE i.codigo = 'PROFESOR'
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO menu_item (id_padre, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'CALIFICACIONES', 'Calificaciones', 'Registro de notas', '/profesor/calificaciones', 'edit', 2, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item i WHERE i.codigo = 'PROFESOR'
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO menu_item (id_padre, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'ASISTENCIA', 'Asistencia', 'Control de asistencia', '/profesor/asistencia', 'clipboard-check', 3, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item i WHERE i.codigo = 'PROFESOR'
ON CONFLICT (codigo) DO NOTHING;

-- =====================================================
-- 4. API PERMISOS PARA SIDEBAR-TARGETS DE ADMIN
-- =====================================================
INSERT INTO api_permiso (menu_item_id, api_base, descripcion, puede_get, puede_post, puede_put, puede_delete, active, created_at, updated_at, created_by)
SELECT m.id, '/api/v1/universidades', 'Gestión de universidades', true, true, true, true, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m WHERE m.codigo = 'UNIVERSIDADES'
ON CONFLICT (menu_item_id, api_base) DO NOTHING;

INSERT INTO api_permiso (menu_item_id, api_base, descripcion, puede_get, puede_post, puede_put, puede_delete, active, created_at, updated_at, created_by)
SELECT m.id, '/api/v1/tipos-unidad', 'Tipos de unidad organizativa', true, true, true, false, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m WHERE m.codigo = 'TIPOS_UNIDAD'
ON CONFLICT (menu_item_id, api_base) DO NOTHING;

INSERT INTO api_permiso (menu_item_id, api_base, descripcion, puede_get, puede_post, puede_put, puede_delete, active, created_at, updated_at, created_by)
SELECT m.id, '/api/v1/tipos-autoridad', 'Tipos de autoridad', true, true, true, false, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m WHERE m.codigo = 'TIPOS_AUTORIDAD'
ON CONFLICT (menu_item_id, api_base) DO NOTHING;

INSERT INTO api_permiso (menu_item_id, api_base, descripcion, puede_get, puede_post, puede_put, puede_delete, active, created_at, updated_at, created_by)
SELECT m.id, '/api/v1/tipos-localizacion', 'Tipos de localización', true, true, true, false, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m WHERE m.codigo = 'TIPOS_LOCALIZACION'
ON CONFLICT (menu_item_id, api_base) DO NOTHING;

INSERT INTO api_permiso (menu_item_id, api_base, descripcion, puede_get, puede_post, puede_put, puede_delete, active, created_at, updated_at, created_by)
SELECT m.id, '/api/v1/unidades-organizativas', 'Unidades organizativas', true, true, true, false, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m WHERE m.codigo = 'UNIDADES_ORGANIZATIVAS'
ON CONFLICT (menu_item_id, api_base) DO NOTHING;

-- =====================================================
-- 5. ASIGNAR MENÚS AL ROL ADMIN
-- =====================================================
-- Asignar isla ADMIN
INSERT INTO rol_menu (rol_nombre, menu_item_id, active, created_at, updated_at, created_by)
SELECT 'ADMIN', m.id, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m WHERE m.codigo = 'ADMIN'
ON CONFLICT (rol_nombre, menu_item_id) DO NOTHING;

-- Asignar todos los sidebar-targets de ADMIN
INSERT INTO rol_menu (rol_nombre, menu_item_id, active, created_at, updated_at, created_by)
SELECT 'ADMIN', m.id, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m 
JOIN menu_item isla ON m.id_padre = isla.id
WHERE isla.codigo = 'ADMIN'
ON CONFLICT (rol_nombre, menu_item_id) DO NOTHING;

-- =====================================================
-- 6. ASIGNAR MENÚS AL ROL PROFESOR
-- =====================================================
INSERT INTO rol_menu (rol_nombre, menu_item_id, active, created_at, updated_at, created_by)
SELECT 'PROFESOR', m.id, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m WHERE m.codigo = 'PROFESOR'
ON CONFLICT (rol_nombre, menu_item_id) DO NOTHING;

INSERT INTO rol_menu (rol_nombre, menu_item_id, active, created_at, updated_at, created_by)
SELECT 'PROFESOR', m.id, true, NOW(), NOW(), 'SYSTEM'
FROM menu_item m 
JOIN menu_item isla ON m.id_padre = isla.id
WHERE isla.codigo = 'PROFESOR'
ON CONFLICT (rol_nombre, menu_item_id) DO NOTHING;
