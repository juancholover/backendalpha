-- Casbin authorization policies table
-- This table stores all RBAC policies for the system

CREATE TABLE IF NOT EXISTS casbin_rule (
    id SERIAL PRIMARY KEY,
    ptype VARCHAR(10) NOT NULL,
    v0 VARCHAR(256),
    v1 VARCHAR(256),
    v2 VARCHAR(256),
    v3 VARCHAR(256),
    v4 VARCHAR(256),
    v5 VARCHAR(256)
);

-- Indices for performance
CREATE INDEX IF NOT EXISTS idx_casbin_ptype ON casbin_rule(ptype);
CREATE INDEX IF NOT EXISTS idx_casbin_v0 ON casbin_rule(v0);
CREATE INDEX IF NOT EXISTS idx_casbin_v0_v1 ON casbin_rule(v0, v1);

-- ============================================
-- SEED: Default Roles Policies
-- ============================================

-- ADMIN: Full access to all endpoints
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'ADMIN', '/api/v1/*', '*');

-- PROFESOR: Academic operations
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'PROFESOR', '/api/v1/evaluacion-notas/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'PROFESOR', '/api/v1/evaluacion-notas/*', 'POST');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'PROFESOR', '/api/v1/evaluacion-notas/*', 'PUT');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'PROFESOR', '/api/v1/evaluacion-criterios/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'PROFESOR', '/api/v1/evaluacion-criterios/*', 'POST');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'PROFESOR', '/api/v1/evaluacion-criterios/*', 'PUT');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'PROFESOR', '/api/v1/asistencias/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'PROFESOR', '/api/v1/asistencias/*', 'POST');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'PROFESOR', '/api/v1/silabos/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'PROFESOR', '/api/v1/cursos-ofertados/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'PROFESOR', '/api/v1/matriculas/*', 'GET');

-- ESTUDIANTE: Limited read access
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'ESTUDIANTE', '/api/v1/evaluacion-notas/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'ESTUDIANTE', '/api/v1/matriculas/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'ESTUDIANTE', '/api/v1/silabos/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'ESTUDIANTE', '/api/v1/cursos-ofertados/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'ESTUDIANTE', '/api/v1/horarios/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'ESTUDIANTE', '/api/v1/periodos-academicos/*', 'GET');

-- SECRETARIA: Administrative operations
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'SECRETARIA', '/api/v1/matriculas/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'SECRETARIA', '/api/v1/matriculas/*', 'POST');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'SECRETARIA', '/api/v1/estudiantes/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'SECRETARIA', '/api/v1/estudiantes/*', 'POST');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'SECRETARIA', '/api/v1/periodos-academicos/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'SECRETARIA', '/api/v1/cursos-ofertados/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'SECRETARIA', '/api/v1/cursos-ofertados/*', 'POST');

-- ============================================
-- SEED: Default User Role Assignments
-- ============================================
-- Assign super admin user to ADMIN role
INSERT INTO casbin_rule (ptype, v0, v1) VALUES ('g', 'admin@upeu.edu.pe', 'ADMIN');

-- ============================================
-- AUTORIDADES UNIVERSITARIAS (Jerarquía)
-- ============================================

-- NIVEL 0: RECTOR (Máxima autoridad)
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'RECTOR', '/api/v1/*', '*');

-- NIVEL 1: VICERRECTORES
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'VICERRECTOR_ACADEMICO', '/api/v1/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'VICERRECTOR_ACADEMICO', '/api/v1/programas-academicos/*', '*');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'VICERRECTOR_ACADEMICO', '/api/v1/cursos/*', '*');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'VICERRECTOR_ACADEMICO', '/api/v1/silabos/*', '*');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'VICERRECTOR_ACADEMICO', '/api/v1/planes-academicos/*', '*');

INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'VICERRECTOR_INVESTIGACION', '/api/v1/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'VICERRECTOR_INVESTIGACION', '/api/v1/silabos/*', '*');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'VICERRECTOR_INVESTIGACION', '/api/v1/profesores/*', 'GET');

-- NIVEL 2: DECANO y SECRETARIO GENERAL
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'DECANO', '/api/v1/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'DECANO', '/api/v1/programas-academicos/*', '*');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'DECANO', '/api/v1/profesores/*', '*');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'DECANO', '/api/v1/silabos/*', '*');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'DECANO', '/api/v1/autoridades/*', 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'SECRETARIO_GENERAL', '/api/v1/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'SECRETARIO_GENERAL', '/api/v1/personas/*', '*');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'SECRETARIO_GENERAL', '/api/v1/empleados/*', '*');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'SECRETARIO_GENERAL', '/api/v1/autoridades/*', '*');

-- NIVEL 3: DIRECTORES
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'DIRECTOR_ESCUELA', '/api/v1/programas-academicos/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'DIRECTOR_ESCUELA', '/api/v1/cursos/*', '*');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'DIRECTOR_ESCUELA', '/api/v1/silabos/*', '*');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'DIRECTOR_ESCUELA', '/api/v1/estudiantes/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'DIRECTOR_ESCUELA', '/api/v1/profesores/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'DIRECTOR_ESCUELA', '/api/v1/matriculas/*', 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'DIRECTOR_POSTGRADO', '/api/v1/programas-academicos/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'DIRECTOR_POSTGRADO', '/api/v1/cursos/*', '*');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'DIRECTOR_POSTGRADO', '/api/v1/silabos/*', '*');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'DIRECTOR_POSTGRADO', '/api/v1/estudiantes/*', 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'DIRECTOR_INVESTIGACION', '/api/v1/silabos/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'DIRECTOR_INVESTIGACION', '/api/v1/profesores/*', 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'DIRECTOR_ADMINISTRATIVO', '/api/v1/empleados/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'DIRECTOR_ADMINISTRATIVO', '/api/v1/localizaciones/*', '*');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'DIRECTOR_ADMINISTRATIVO', '/api/v1/unidades-organizativas/*', 'GET');

-- NIVEL 4: COORDINADORES Y JEFES
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'COORDINADOR_ACADEMICO', '/api/v1/cursos-ofertados/*', '*');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'COORDINADOR_ACADEMICO', '/api/v1/horarios/*', '*');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'COORDINADOR_ACADEMICO', '/api/v1/matriculas/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'COORDINADOR_ACADEMICO', '/api/v1/silabos/*', 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'JEFE_DEPARTAMENTO', '/api/v1/profesores/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'JEFE_DEPARTAMENTO', '/api/v1/cursos/*', 'GET');
INSERT INTO casbin_rule (ptype, v0, v1, v2) VALUES ('p', 'JEFE_DEPARTAMENTO', '/api/v1/silabos/*', '*');
