-- =====================================================
-- V024__drop_old_permission_tables.sql
-- Eliminar tablas antiguas del esquema de permisos V020
-- Reemplazadas por el esquema simplificado V022
-- =====================================================

-- Eliminar tablas de permisos individuales primero (por FK)
DROP TABLE IF EXISTS usuario_accion CASCADE;
DROP TABLE IF EXISTS usuario_recurso CASCADE;
DROP TABLE IF EXISTS usuario_isla CASCADE;

-- Eliminar tablas de roles
DROP TABLE IF EXISTS rol_accion CASCADE;
DROP TABLE IF EXISTS rol_recurso CASCADE;
DROP TABLE IF EXISTS rol_isla CASCADE;

-- Eliminar tablas principales
DROP TABLE IF EXISTS accion CASCADE;
DROP TABLE IF EXISTS recurso CASCADE;
DROP TABLE IF EXISTS modulo CASCADE;
DROP TABLE IF EXISTS isla CASCADE;

-- =====================================================
-- NOTA: Las siguientes tablas ahora reemplazan a las anteriores:
-- - menu_item (reemplaza isla + modulo + recurso)
-- - api_permiso (reemplaza accion)
-- - rol_menu (reemplaza rol_isla + rol_recurso + rol_accion)
-- - usuario_menu (reemplaza usuario_isla + usuario_recurso + usuario_accion)
-- =====================================================
