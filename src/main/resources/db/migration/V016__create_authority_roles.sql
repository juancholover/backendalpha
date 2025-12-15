-- V016: Policies for Authority Roles and Core Roles

-- Clean up any potential duplicates first (optional)
-- DELETE FROM casbin_rule WHERE ptype = 'p' AND v0 IN ('DEC','SG','DE','DP','DI','DA','CA','JD');

-- Base permissions for Authorities (Using codes from TipoAutoridad)
-- DEC = Decano
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES 
('p', 'DEC', '/api/v1/autoridades/*', 'GET'),
('p', 'DEC', '/api/v1/programas-academicos/*', '*'),
('p', 'DEC', '/api/v1/profesores/*', 'GET'),
('p', 'DEC', '/api/v1/estudiantes/*', 'GET');

-- SG = Secretario General
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES 
('p', 'SG', '/api/v1/autoridades/*', '*'),
('p', 'SG', '/api/v1/grados-titulos/*', '*');

-- DE = Director Escuela
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES 
('p', 'DE', '/api/v1/estudiantes/*', '*'),
('p', 'DE', '/api/v1/profesores/*', 'GET'),
('p', 'DE', '/api/v1/horarios/*', '*');

-- DP = Director Postgrado
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES 
('p', 'DP', '/api/v1/programas-academicos/postgrado/*', '*');

-- DI = Director Investigacion
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES 
('p', 'DI', '/api/v1/investigacion/*', '*');

-- DA = Director Administrativo
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES 
('p', 'DA', '/api/v1/pagos/*', '*'),
('p', 'DA', '/api/v1/presupuestos/*', '*');

-- CA = Coordinador Académico
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES 
('p', 'CA', '/api/v1/silabos/*', '*'),
('p', 'CA', '/api/v1/notas/*', 'GET');

-- JD = Jefe Departamento
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES 
('p', 'JD', '/api/v1/profesores/*', 'GET'),
('p', 'JD', '/api/v1/carga-lectiva/*', '*');

-- Core Roles Permissions (if not present)
-- ESTUDIANTE
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES 
('p', 'ESTUDIANTE', '/api/v1/notas/mis-notas', 'GET'),
('p', 'ESTUDIANTE', '/api/v1/matriculas/mis-matriculas', 'GET'),
('p', 'ESTUDIANTE', '/api/v1/perfil/me', 'GET');

-- EMPLEADO
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES 
('p', 'EMPLEADO', '/api/v1/perfil/me', 'GET');

-- PROFESOR
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES 
('p', 'PROFESOR', '/api/v1/notas/*', '*'),
('p', 'PROFESOR', '/api/v1/silabos/*', '*');
