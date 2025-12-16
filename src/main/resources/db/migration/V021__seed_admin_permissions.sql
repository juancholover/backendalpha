-- =====================================================
-- V021__seed_admin_permissions.sql
-- Datos iniciales para el super admin
-- =====================================================

-- =====================================================
-- 1. CREAR ISLA ADMIN
-- =====================================================
INSERT INTO isla (codigo, nombre, descripcion, icono, color, ruta_default, es_isla_principal, orden, active, created_at, updated_at, created_by)
VALUES ('ADMIN', 'Administrador', 'Administración completa del sistema', 'shield-check', '#DC2626', '/admin/dashboard', true, 1, true, NOW(), NOW(), 'SYSTEM')
ON CONFLICT (codigo) DO NOTHING;

-- =====================================================
-- 2. CREAR MÓDULOS DENTRO DE ISLA ADMIN
-- =====================================================
INSERT INTO modulo (isla_id, codigo, nombre, descripcion, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'UNIVERSIDADES', 'Universidades', 'Gestión de universidades', 'building', 1, true, NOW(), NOW(), 'SYSTEM'
FROM isla i WHERE i.codigo = 'ADMIN'
ON CONFLICT (isla_id, codigo) DO NOTHING;

INSERT INTO modulo (isla_id, codigo, nombre, descripcion, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'ORGANIZACION', 'Organización', 'Estructura organizativa', 'sitemap', 2, true, NOW(), NOW(), 'SYSTEM'
FROM isla i WHERE i.codigo = 'ADMIN'
ON CONFLICT (isla_id, codigo) DO NOTHING;

INSERT INTO modulo (isla_id, codigo, nombre, descripcion, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'AUTORIDADES', 'Autoridades', 'Gestión de autoridades universitarias', 'user-tie', 3, true, NOW(), NOW(), 'SYSTEM'
FROM isla i WHERE i.codigo = 'ADMIN'
ON CONFLICT (isla_id, codigo) DO NOTHING;

INSERT INTO modulo (isla_id, codigo, nombre, descripcion, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'UBICACION', 'Ubicación', 'Gestión de ubicaciones', 'map-pin', 4, true, NOW(), NOW(), 'SYSTEM'
FROM isla i WHERE i.codigo = 'ADMIN'
ON CONFLICT (isla_id, codigo) DO NOTHING;

INSERT INTO modulo (isla_id, codigo, nombre, descripcion, icono, orden, active, created_at, updated_at, created_by)
SELECT i.id, 'PERMISOS', 'Permisos', 'Gestión de roles y permisos', 'lock', 5, true, NOW(), NOW(), 'SYSTEM'
FROM isla i WHERE i.codigo = 'ADMIN'
ON CONFLICT (isla_id, codigo) DO NOTHING;

-- =====================================================
-- 3. CREAR RECURSOS (Sidebar targets) PARA UNIVERSIDADES
-- =====================================================
INSERT INTO recurso (modulo_id, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT m.id, 'VER_UNIVERSIDADES', 'Ver Universidades', 'Listar universidades', '/admin/universidades', 'building', 1, true, NOW(), NOW(), 'SYSTEM'
FROM modulo m JOIN isla i ON m.isla_id = i.id WHERE i.codigo = 'ADMIN' AND m.codigo = 'UNIVERSIDADES'
ON CONFLICT (modulo_id, codigo) DO NOTHING;

-- =====================================================
-- 4. CREAR ACCIONES PARA RECURSO VER_UNIVERSIDADES
-- =====================================================
INSERT INTO accion (recurso_id, codigo, nombre, descripcion, endpoint, metodo_http, active, created_at, updated_at, created_by)
SELECT r.id, 'READ', 'Listar', 'Obtener lista de universidades', '/api/v1/universidades', 'GET', true, NOW(), NOW(), 'SYSTEM'
FROM recurso r JOIN modulo m ON r.modulo_id = m.id JOIN isla i ON m.isla_id = i.id
WHERE i.codigo = 'ADMIN' AND m.codigo = 'UNIVERSIDADES' AND r.codigo = 'VER_UNIVERSIDADES'
ON CONFLICT (recurso_id, codigo) DO NOTHING;

INSERT INTO accion (recurso_id, codigo, nombre, descripcion, endpoint, metodo_http, active, created_at, updated_at, created_by)
SELECT r.id, 'CREATE', 'Crear', 'Crear nueva universidad', '/api/v1/universidades', 'POST', true, NOW(), NOW(), 'SYSTEM'
FROM recurso r JOIN modulo m ON r.modulo_id = m.id JOIN isla i ON m.isla_id = i.id
WHERE i.codigo = 'ADMIN' AND m.codigo = 'UNIVERSIDADES' AND r.codigo = 'VER_UNIVERSIDADES'
ON CONFLICT (recurso_id, codigo) DO NOTHING;

INSERT INTO accion (recurso_id, codigo, nombre, descripcion, endpoint, metodo_http, active, created_at, updated_at, created_by)
SELECT r.id, 'UPDATE', 'Actualizar', 'Actualizar universidad existente', '/api/v1/universidades/:id', 'PUT', true, NOW(), NOW(), 'SYSTEM'
FROM recurso r JOIN modulo m ON r.modulo_id = m.id JOIN isla i ON m.isla_id = i.id
WHERE i.codigo = 'ADMIN' AND m.codigo = 'UNIVERSIDADES' AND r.codigo = 'VER_UNIVERSIDADES'
ON CONFLICT (recurso_id, codigo) DO NOTHING;

INSERT INTO accion (recurso_id, codigo, nombre, descripcion, endpoint, metodo_http, active, created_at, updated_at, created_by)
SELECT r.id, 'DELETE', 'Eliminar', 'Eliminar universidad', '/api/v1/universidades/:id', 'DELETE', true, NOW(), NOW(), 'SYSTEM'
FROM recurso r JOIN modulo m ON r.modulo_id = m.id JOIN isla i ON m.isla_id = i.id
WHERE i.codigo = 'ADMIN' AND m.codigo = 'UNIVERSIDADES' AND r.codigo = 'VER_UNIVERSIDADES'
ON CONFLICT (recurso_id, codigo) DO NOTHING;

-- =====================================================
-- 5. CREAR RECURSOS PARA ORGANIZACIÓN
-- =====================================================
INSERT INTO recurso (modulo_id, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT m.id, 'UNIDADES_ORGANIZATIVAS', 'Unidades Organizativas', 'Gestión de unidades organizativas', '/admin/unidades-organizativas', 'sitemap', 1, true, NOW(), NOW(), 'SYSTEM'
FROM modulo m JOIN isla i ON m.isla_id = i.id WHERE i.codigo = 'ADMIN' AND m.codigo = 'ORGANIZACION'
ON CONFLICT (modulo_id, codigo) DO NOTHING;

INSERT INTO recurso (modulo_id, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT m.id, 'TIPOS_UNIDAD', 'Tipos de Unidad', 'Catálogo de tipos de unidad', '/admin/tipos-unidad', 'tags', 2, true, NOW(), NOW(), 'SYSTEM'
FROM modulo m JOIN isla i ON m.isla_id = i.id WHERE i.codigo = 'ADMIN' AND m.codigo = 'ORGANIZACION'
ON CONFLICT (modulo_id, codigo) DO NOTHING;

-- =====================================================
-- 6. CREAR RECURSOS PARA AUTORIDADES
-- =====================================================
INSERT INTO recurso (modulo_id, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT m.id, 'TIPOS_AUTORIDAD', 'Tipos de Autoridad', 'Catálogo de tipos de autoridad', '/admin/tipos-autoridad', 'crown', 1, true, NOW(), NOW(), 'SYSTEM'
FROM modulo m JOIN isla i ON m.isla_id = i.id WHERE i.codigo = 'ADMIN' AND m.codigo = 'AUTORIDADES'
ON CONFLICT (modulo_id, codigo) DO NOTHING;

INSERT INTO recurso (modulo_id, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT m.id, 'AUTORIDADES', 'Autoridades', 'Gestión de autoridades', '/admin/autoridades', 'user-tie', 2, true, NOW(), NOW(), 'SYSTEM'
FROM modulo m JOIN isla i ON m.isla_id = i.id WHERE i.codigo = 'ADMIN' AND m.codigo = 'AUTORIDADES'
ON CONFLICT (modulo_id, codigo) DO NOTHING;

-- =====================================================
-- 7. CREAR RECURSOS PARA UBICACIÓN
-- =====================================================
INSERT INTO recurso (modulo_id, codigo, nombre, descripcion, ruta_frontend, icono, orden, active, created_at, updated_at, created_by)
SELECT m.id, 'TIPOS_LOCALIZACION', 'Tipos de Localización', 'Catálogo de tipos de localización', '/admin/tipos-localizacion', 'map', 1, true, NOW(), NOW(), 'SYSTEM'
FROM modulo m JOIN isla i ON m.isla_id = i.id WHERE i.codigo = 'ADMIN' AND m.codigo = 'UBICACION'
ON CONFLICT (modulo_id, codigo) DO NOTHING;

-- =====================================================
-- 8. ASIGNAR ISLA ADMIN AL ROL ADMIN
-- =====================================================
INSERT INTO rol_isla (rol_nombre, isla_id, active, created_at, updated_at, created_by)
SELECT 'ADMIN', i.id, true, NOW(), NOW(), 'SYSTEM'
FROM isla i WHERE i.codigo = 'ADMIN'
ON CONFLICT (rol_nombre, isla_id) DO NOTHING;

-- =====================================================
-- 9. ASIGNAR TODOS LOS RECURSOS AL ROL ADMIN
-- =====================================================
INSERT INTO rol_recurso (rol_nombre, recurso_id, active, created_at, updated_at, created_by)
SELECT 'ADMIN', r.id, true, NOW(), NOW(), 'SYSTEM'
FROM recurso r
JOIN modulo m ON r.modulo_id = m.id
JOIN isla i ON m.isla_id = i.id
WHERE i.codigo = 'ADMIN'
ON CONFLICT (rol_nombre, recurso_id) DO NOTHING;

-- =====================================================
-- 10. ASIGNAR TODAS LAS ACCIONES AL ROL ADMIN
-- =====================================================
INSERT INTO rol_accion (rol_nombre, accion_id, permitido, active, created_at, updated_at, created_by)
SELECT 'ADMIN', a.id, true, true, NOW(), NOW(), 'SYSTEM'
FROM accion a
JOIN recurso r ON a.recurso_id = r.id
JOIN modulo m ON r.modulo_id = m.id
JOIN isla i ON m.isla_id = i.id
WHERE i.codigo = 'ADMIN'
ON CONFLICT (rol_nombre, accion_id) DO NOTHING;

