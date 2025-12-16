-- =====================================================
-- V032: Asegurar acceso total para SUPERADMIN
-- =====================================================
-- Aunque normalmente lo tiene, esto garantiza que pueda acceder a las nuevas rutas de admin

INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'SUPERADMIN', '/api/v1/*', '*');
