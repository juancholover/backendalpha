-- =====================================================
-- V031: Permisos Casbin para APIs de administración de permisos
-- =====================================================
-- Solo permisos, sin datos de ejemplo

-- Permisos para SUPERADMIN (ya tiene wildcard /api/v1/*)
-- No necesita permisos adicionales

-- Permisos para ADMIN - acceso a gestión de roles
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'ADMIN', '/api/v1/admin/roles', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'ADMIN', '/api/v1/admin/roles/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'ADMIN', '/api/v1/admin/roles/*', 'PUT');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'ADMIN', '/api/v1/admin/roles/*', 'POST');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'ADMIN', '/api/v1/admin/roles/*', 'DELETE');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'ADMIN', '/api/v1/admin/modulos', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'ADMIN', '/api/v1/admin/modulos/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'ADMIN', '/api/v1/admin/usuarios', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'ADMIN', '/api/v1/admin/usuarios/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'ADMIN', '/api/v1/admin/usuarios/*', 'POST');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'ADMIN', '/api/v1/admin/usuarios/*', 'DELETE');
