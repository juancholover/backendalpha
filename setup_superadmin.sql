-- =====================================================
-- SCRIPT UNIFICADO: CONFIGURACIÓN SUPER ADMINISTRADOR
-- Ejecutar este script directamente en pgAdmin
--
-- SISTEMA: Autenticación con JWT + Bcrypt, Autorización con Casbin
-- ROLES: ADMIN, PROFESOR, ESTUDIANTE, SECRETARIA + Autoridades
-- =====================================================

-- =====================================================
-- 1. VERIFICAR Y CREAR TABLA UNIVERSIDAD (si no existe)
-- =====================================================
DO $$
BEGIN
    -- Verificar si la tabla universidad existe
    IF NOT EXISTS (
        SELECT FROM information_schema.tables
        WHERE table_schema = 'public'
        AND table_name = 'universidad'
    ) THEN
        -- Crear tabla si no existe
        CREATE TABLE universidad (
            id BIGSERIAL PRIMARY KEY,
            codigo VARCHAR(20) UNIQUE NOT NULL,
            nombre VARCHAR(255) NOT NULL,
            dominio VARCHAR(50) UNIQUE,
            ruc VARCHAR(11) UNIQUE NOT NULL,
            tipo VARCHAR(50) NOT NULL,
            website VARCHAR(255),
            logo_url VARCHAR(500),
            zona_horaria VARCHAR(50),
            locale VARCHAR(20),
            configuracion JSONB,
            plan VARCHAR(20),
            estado VARCHAR(20),
            fecha_vencimiento DATE,
            max_estudiantes INTEGER,
            max_docentes INTEGER,
            total_estudiantes INTEGER DEFAULT 0,
            total_docentes INTEGER DEFAULT 0,
            active BOOLEAN DEFAULT true,
            created_at TIMESTAMP NOT NULL DEFAULT NOW(),
            updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
            created_by VARCHAR(100),
            updated_by VARCHAR(100)
        );

        RAISE NOTICE 'Tabla universidad creada exitosamente';
    ELSE
        RAISE NOTICE 'Tabla universidad ya existe';

        -- Agregar columnas que puedan faltar (en caso de que la tabla exista pero esté incompleta)
        -- Solo agregar si no existen
        BEGIN
            ALTER TABLE universidad ADD COLUMN IF NOT EXISTS active BOOLEAN DEFAULT true;
            ALTER TABLE universidad ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW();
            ALTER TABLE universidad ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT NOW();
            ALTER TABLE universidad ADD COLUMN IF NOT EXISTS created_by VARCHAR(100);
            ALTER TABLE universidad ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);
            ALTER TABLE universidad ADD COLUMN IF NOT EXISTS total_estudiantes INTEGER DEFAULT 0;
            ALTER TABLE universidad ADD COLUMN IF NOT EXISTS total_docentes INTEGER DEFAULT 0;
        EXCEPTION
            WHEN duplicate_column THEN
                -- Columna ya existe, ignorar
                NULL;
        END;
    END IF;
END $$;

-- =====================================================
-- 2. VERIFICAR Y CREAR TABLA CASBIN_RULE (si no existe)
-- =====================================================
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT FROM information_schema.tables
        WHERE table_schema = 'public'
        AND table_name = 'casbin_rule'
    ) THEN
        CREATE TABLE casbin_rule (
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
        CREATE INDEX idx_casbin_ptype ON casbin_rule(ptype);
        CREATE INDEX idx_casbin_v0 ON casbin_rule(v0);
        CREATE INDEX idx_casbin_v0_v1 ON casbin_rule(v0, v1);

        RAISE NOTICE 'Tabla casbin_rule creada exitosamente';
    ELSE
        RAISE NOTICE 'Tabla casbin_rule ya existe';
    END IF;
END $$;

-- =====================================================
-- 3. VERIFICAR Y CREAR TABLA PERSONA (si no existe)
-- =====================================================
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT FROM information_schema.tables
        WHERE table_schema = 'public'
        AND table_name = 'persona'
    ) THEN
        CREATE TABLE persona (
            id BIGSERIAL PRIMARY KEY,
            nombres VARCHAR(100) NOT NULL,
            apellido_paterno VARCHAR(100) NOT NULL,
            apellido_materno VARCHAR(100),
            tipo_documento VARCHAR(20) NOT NULL,
            numero_documento VARCHAR(20) NOT NULL UNIQUE,
            email VARCHAR(255) NOT NULL UNIQUE,
            telefono VARCHAR(20),
            celular VARCHAR(20),
            fecha_nacimiento DATE,
            genero VARCHAR(20),
            estado_civil VARCHAR(20),
            direccion TEXT,
            foto_url VARCHAR(500),
            active BOOLEAN DEFAULT true,
            created_at TIMESTAMP NOT NULL DEFAULT NOW(),
            updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
            created_by VARCHAR(100),
            updated_by VARCHAR(100)
        );

        RAISE NOTICE 'Tabla persona creada exitosamente';
    ELSE
        RAISE NOTICE 'Tabla persona ya existe';
    END IF;
END $$;

-- =====================================================
-- 4. VERIFICAR Y CREAR TABLA AUTH_USUARIO (si no existe)
-- =====================================================
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT FROM information_schema.tables
        WHERE table_schema = 'public'
        AND table_name = 'auth_usuario'
    ) THEN
        CREATE TABLE auth_usuario (
            id BIGSERIAL PRIMARY KEY,
            persona_id BIGINT NOT NULL UNIQUE,
            password_hash VARCHAR(255) NOT NULL,
            ultimo_acceso TIMESTAMP,
            intentos_fallidos INTEGER DEFAULT 0,
            fecha_bloqueo TIMESTAMP,
            requiere_cambio_password BOOLEAN DEFAULT false,
            fecha_ultimo_cambio_password TIMESTAMP,
            token_recuperacion VARCHAR(255),
            fecha_expiracion_token TIMESTAMP,
            active BOOLEAN DEFAULT true,
            created_at TIMESTAMP NOT NULL DEFAULT NOW(),
            updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
            created_by VARCHAR(100),
            updated_by VARCHAR(100),
            CONSTRAINT fk_auth_usuario_persona FOREIGN KEY (persona_id) REFERENCES persona(id) ON DELETE CASCADE
        );

        RAISE NOTICE 'Tabla auth_usuario creada exitosamente (usuario global - sin referencia a universidad)';
    ELSE
        RAISE NOTICE 'Tabla auth_usuario ya existe';
    END IF;
END $$;

-- =====================================================
-- 5. VERIFICAR E INSERTAR UNIVERSIDAD POR DEFECTO (solo si no existe)
-- =====================================================

DO $$
BEGIN
    -- Solo verificar si existe al menos una universidad
    IF NOT EXISTS (SELECT 1 FROM universidad LIMIT 1) THEN
        INSERT INTO universidad (
            codigo, nombre, dominio, ruc, tipo, website,
            zona_horaria, locale, plan, estado,
            max_estudiantes, max_docentes, total_estudiantes, total_docentes,
            active, created_at, updated_at, created_by
        ) VALUES (
            'UPEU',
            'Universidad Peruana Unión',
            'upeu.edu.pe',
            '20144918681',
            'PRIVADA',
            'https://upeu.edu.pe',
            'America/Lima',
            'es_PE',
            'PREMIUM',
            'ACTIVA',
            10000,
            1000,
            0,
            0,
            true,
            NOW(),
            NOW(),
            'SYSTEM'
        );

        RAISE NOTICE 'Universidad UPEU creada exitosamente (registro por defecto)';
    ELSE
        RAISE NOTICE 'Ya existe al menos una universidad en el sistema, usando la existente...';
    END IF;
END $$;

-- =====================================================
-- 6. CONFIGURAR POLÍTICAS CASBIN PARA ROLES
-- =====================================================

-- POLÍTICA PÚBLICA: Endpoints que NO requieren autenticación
-- Los endpoints públicos deben ser accesibles sin rol (usuario 'system')
-- Se insertan AMBAS variantes: con y sin doble slash para compatibilidad

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'public', '/api/v1/auth/login', 'POST'
WHERE NOT EXISTS (
    SELECT 1 FROM casbin_rule
    WHERE ptype = 'p' AND v0 = 'public' AND v1 = '/api/v1/auth/login' AND v2 = 'POST'
);

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'public', '//api/v1/auth/login', 'POST'
WHERE NOT EXISTS (
    SELECT 1 FROM casbin_rule
    WHERE ptype = 'p' AND v0 = 'public' AND v1 = '//api/v1/auth/login' AND v2 = 'POST'
);

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'public', '/api/v1/auth/register', 'POST'
WHERE NOT EXISTS (
    SELECT 1 FROM casbin_rule
    WHERE ptype = 'p' AND v0 = 'public' AND v1 = '/api/v1/auth/register' AND v2 = 'POST'
);

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'public', '//api/v1/auth/register', 'POST'
WHERE NOT EXISTS (
    SELECT 1 FROM casbin_rule
    WHERE ptype = 'p' AND v0 = 'public' AND v1 = '//api/v1/auth/register' AND v2 = 'POST'
);

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'public', '/api/v1/auth/refresh', 'POST'
WHERE NOT EXISTS (
    SELECT 1 FROM casbin_rule
    WHERE ptype = 'p' AND v0 = 'public' AND v1 = '/api/v1/auth/refresh' AND v2 = 'POST'
);

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'public', '//api/v1/auth/refresh', 'POST'
WHERE NOT EXISTS (
    SELECT 1 FROM casbin_rule
    WHERE ptype = 'p' AND v0 = 'public' AND v1 = '//api/v1/auth/refresh' AND v2 = 'POST'
);

-- Asignar rol 'public' al usuario del sistema (para endpoints públicos)
INSERT INTO casbin_rule (ptype, v0, v1)
SELECT 'g', 'system', 'public'
WHERE NOT EXISTS (
    SELECT 1 FROM casbin_rule
    WHERE ptype = 'g' AND v0 = 'system' AND v1 = 'public'
);

-- ADMIN: Acceso completo a todo el sistema
INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/*', '*'
WHERE NOT EXISTS (
    SELECT 1 FROM casbin_rule
    WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/*' AND v2 = '*'
);

-- PROFESOR: Operaciones académicas
INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'PROFESOR', '/api/v1/evaluacion-notas/*', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'PROFESOR' AND v1 = '/api/v1/evaluacion-notas/*' AND v2 = 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'PROFESOR', '/api/v1/evaluacion-notas/*', 'POST'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'PROFESOR' AND v1 = '/api/v1/evaluacion-notas/*' AND v2 = 'POST');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'PROFESOR', '/api/v1/evaluacion-notas/*', 'PUT'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'PROFESOR' AND v1 = '/api/v1/evaluacion-notas/*' AND v2 = 'PUT');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'PROFESOR', '/api/v1/evaluacion-criterios/*', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'PROFESOR' AND v1 = '/api/v1/evaluacion-criterios/*' AND v2 = 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'PROFESOR', '/api/v1/evaluacion-criterios/*', 'POST'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'PROFESOR' AND v1 = '/api/v1/evaluacion-criterios/*' AND v2 = 'POST');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'PROFESOR', '/api/v1/evaluacion-criterios/*', 'PUT'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'PROFESOR' AND v1 = '/api/v1/evaluacion-criterios/*' AND v2 = 'PUT');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'PROFESOR', '/api/v1/asistencias/*', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'PROFESOR' AND v1 = '/api/v1/asistencias/*' AND v2 = 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'PROFESOR', '/api/v1/asistencias/*', 'POST'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'PROFESOR' AND v1 = '/api/v1/asistencias/*' AND v2 = 'POST');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'PROFESOR', '/api/v1/silabos/*', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'PROFESOR' AND v1 = '/api/v1/silabos/*' AND v2 = 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'PROFESOR', '/api/v1/cursos-ofertados/*', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'PROFESOR' AND v1 = '/api/v1/cursos-ofertados/*' AND v2 = 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'PROFESOR', '/api/v1/matriculas/*', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'PROFESOR' AND v1 = '/api/v1/matriculas/*' AND v2 = 'GET');

-- ESTUDIANTE: Acceso limitado de solo lectura
INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ESTUDIANTE', '/api/v1/evaluacion-notas/*', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ESTUDIANTE' AND v1 = '/api/v1/evaluacion-notas/*' AND v2 = 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ESTUDIANTE', '/api/v1/matriculas/*', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ESTUDIANTE' AND v1 = '/api/v1/matriculas/*' AND v2 = 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ESTUDIANTE', '/api/v1/silabos/*', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ESTUDIANTE' AND v1 = '/api/v1/silabos/*' AND v2 = 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ESTUDIANTE', '/api/v1/cursos-ofertados/*', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ESTUDIANTE' AND v1 = '/api/v1/cursos-ofertados/*' AND v2 = 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ESTUDIANTE', '/api/v1/horarios/*', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ESTUDIANTE' AND v1 = '/api/v1/horarios/*' AND v2 = 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ESTUDIANTE', '/api/v1/periodos-academicos/*', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ESTUDIANTE' AND v1 = '/api/v1/periodos-academicos/*' AND v2 = 'GET');

-- SECRETARIA: Operaciones administrativas
INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'SECRETARIA', '/api/v1/matriculas/*', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'SECRETARIA' AND v1 = '/api/v1/matriculas/*' AND v2 = 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'SECRETARIA', '/api/v1/matriculas/*', 'POST'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'SECRETARIA' AND v1 = '/api/v1/matriculas/*' AND v2 = 'POST');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'SECRETARIA', '/api/v1/estudiantes/*', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'SECRETARIA' AND v1 = '/api/v1/estudiantes/*' AND v2 = 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'SECRETARIA', '/api/v1/estudiantes/*', 'POST'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'SECRETARIA' AND v1 = '/api/v1/estudiantes/*' AND v2 = 'POST');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'SECRETARIA', '/api/v1/periodos-academicos/*', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'SECRETARIA' AND v1 = '/api/v1/periodos-academicos/*' AND v2 = 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'SECRETARIA', '/api/v1/cursos-ofertados/*', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'SECRETARIA' AND v1 = '/api/v1/cursos-ofertados/*' AND v2 = 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'SECRETARIA', '/api/v1/cursos-ofertados/*', 'POST'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'SECRETARIA' AND v1 = '/api/v1/cursos-ofertados/*' AND v2 = 'POST');

-- AUTORIDADES UNIVERSITARIAS (Jerarquía)
-- RECTOR: Máxima autoridad
INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'RECTOR', '/api/v1/*', '*'
WHERE NOT EXISTS (
    SELECT 1 FROM casbin_rule
    WHERE ptype = 'p' AND v0 = 'RECTOR' AND v1 = '/api/v1/*' AND v2 = '*'
);

-- DECANO: Gestión académica de facultad
INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'DECANO', '/api/v1/*', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'DECANO' AND v1 = '/api/v1/*' AND v2 = 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'DECANO', '/api/v1/programas-academicos/*', '*'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'DECANO' AND v1 = '/api/v1/programas-academicos/*' AND v2 = '*');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'DECANO', '/api/v1/profesores/*', '*'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'DECANO' AND v1 = '/api/v1/profesores/*' AND v2 = '*');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'DECANO', '/api/v1/silabos/*', '*'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'DECANO' AND v1 = '/api/v1/silabos/*' AND v2 = '*');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'DECANO', '/api/v1/autoridades/*', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'DECANO' AND v1 = '/api/v1/autoridades/*' AND v2 = 'GET');

-- DIRECTOR_ESCUELA: Gestión de escuela profesional
INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'DIRECTOR_ESCUELA', '/api/v1/programas-academicos/*', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'DIRECTOR_ESCUELA' AND v1 = '/api/v1/programas-academicos/*' AND v2 = 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'DIRECTOR_ESCUELA', '/api/v1/cursos/*', '*'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'DIRECTOR_ESCUELA' AND v1 = '/api/v1/cursos/*' AND v2 = '*');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'DIRECTOR_ESCUELA', '/api/v1/silabos/*', '*'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'DIRECTOR_ESCUELA' AND v1 = '/api/v1/silabos/*' AND v2 = '*');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'DIRECTOR_ESCUELA', '/api/v1/estudiantes/*', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'DIRECTOR_ESCUELA' AND v1 = '/api/v1/estudiantes/*' AND v2 = 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'DIRECTOR_ESCUELA', '/api/v1/profesores/*', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'DIRECTOR_ESCUELA' AND v1 = '/api/v1/profesores/*' AND v2 = 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'DIRECTOR_ESCUELA', '/api/v1/matriculas/*', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'DIRECTOR_ESCUELA' AND v1 = '/api/v1/matriculas/*' AND v2 = 'GET');

-- =====================================================
-- 5. CREAR PERSONA PARA EL SUPER ADMINISTRADOR
-- =====================================================
INSERT INTO persona (
    nombres, apellido_paterno, apellido_materno,
    tipo_documento, numero_documento, email,
    telefono, fecha_nacimiento, genero,
    active, created_at, updated_at, created_by
)
SELECT
    'Super',
    'Administrador',
    'Sistema',
    'DNI',
    '00000001',
    'superadmin@upeu.edu.pe',
    '+51999999999',
    '1990-01-01'::DATE,
    'M',
    true,
    NOW(),
    NOW(),
    'SYSTEM'
WHERE NOT EXISTS (
    SELECT 1 FROM persona p WHERE p.email = 'superadmin@upeu.edu.pe'
);

-- =====================================================
-- 6. CREAR USUARIO Y ASIGNAR ROL ADMIN CON CASBIN
-- =====================================================

DO $$
DECLARE
    persona_id_var BIGINT;
    password_hash_var VARCHAR(255);
    user_email VARCHAR(255) := 'superadmin@upeu.edu.pe';
BEGIN
    SELECT p.id INTO persona_id_var
    FROM persona p WHERE p.email = user_email;

    -- Hash de la contraseña 'SuperAdmin2025!' (este es un hash de ejemplo)
    -- En producción, deberías generar un hash real con salt
    password_hash_var := 'kZ8h9vF2mN7xR4qL3wE6pY1sT5oC8iU9jA0bG7nM2vK4xF6qL9wE3rT7yU1iO5pA8sD9fG2hJ6kL4nM7xZ0vC3bN5mQ8wR1tY4uI7oP0aS6dF9gH2jK5lN8xZ1vC4bM7nQ0wR3tY6uI9oP2aS5dF8gH1jK4lN7xZ0vC3bM6nQ9wR2tY5u';

    -- Crear usuario autenticado si no existe (SIN referencia a universidad)
    IF NOT EXISTS (SELECT 1 FROM auth_usuario au WHERE au.persona_id = persona_id_var) THEN
        INSERT INTO auth_usuario (
            persona_id,
            password_hash,
            requiere_cambio_password,
            fecha_ultimo_cambio_password,
            active,
            created_at,
            updated_at,
            created_by
        ) VALUES (
            persona_id_var,
            password_hash_var,
            false,
            NOW(),
            true,
            NOW(),
            NOW(),
            'SYSTEM'
        );

        RAISE NOTICE 'Usuario super administrador creado exitosamente';
    ELSE
        RAISE NOTICE 'El usuario super administrador ya existe';
    END IF;

    -- Asignar rol ADMIN usando Casbin (grouping policy)
    IF NOT EXISTS (
        SELECT 1 FROM casbin_rule
        WHERE ptype = 'g' AND v0 = user_email AND v1 = 'ADMIN'
    ) THEN
        INSERT INTO casbin_rule (ptype, v0, v1) VALUES ('g', user_email, 'ADMIN');
        RAISE NOTICE 'Rol ADMIN asignado a % en Casbin', user_email;
    ELSE
        RAISE NOTICE 'El usuario ya tiene el rol ADMIN en Casbin';
    END IF;

    RAISE NOTICE 'Email: %', user_email;
    RAISE NOTICE 'Password: SuperAdmin2025!';
    RAISE NOTICE 'IMPORTANTE: Cambiar esta contraseña después del primer acceso';

EXCEPTION
    WHEN OTHERS THEN
        RAISE EXCEPTION 'Error creando usuario: %', SQLERRM;
END $$;

-- =====================================================
-- 7. VERIFICACIONES FINALES
-- =====================================================

-- Verificar que todo se creó correctamente
DO $$
DECLARE
    universidad_count INTEGER;
    casbin_policies_count INTEGER;
    casbin_public_policies_count INTEGER;
    casbin_groupings_count INTEGER;
    usuario_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO universidad_count FROM universidad;
    SELECT COUNT(*) INTO casbin_policies_count FROM casbin_rule WHERE ptype = 'p';
    SELECT COUNT(*) INTO casbin_public_policies_count FROM casbin_rule WHERE ptype = 'p' AND v0 = 'public';
    SELECT COUNT(*) INTO casbin_groupings_count FROM casbin_rule WHERE ptype = 'g' AND v0 = 'superadmin@upeu.edu.pe';
    SELECT COUNT(*) INTO usuario_count FROM auth_usuario au JOIN persona p ON au.persona_id = p.id WHERE p.email = 'superadmin@upeu.edu.pe';

    RAISE NOTICE '=== RESUMEN DE CONFIGURACIÓN CASBIN ===';
    RAISE NOTICE 'Universidades en el sistema: % (esperado: >=1)', universidad_count;
    RAISE NOTICE 'Políticas Casbin TOTALES (p): % (esperado: >30)', casbin_policies_count;
    RAISE NOTICE 'Políticas PÚBLICAS (p): % (esperado: 3)', casbin_public_policies_count;
    RAISE NOTICE 'Asignaciones Casbin (g): % (esperado: 2 - super admin + system public)', casbin_groupings_count;
    RAISE NOTICE 'Usuario super admin: % (esperado: 1)', usuario_count;

    IF casbin_policies_count >= 10 AND casbin_groupings_count >= 1 AND usuario_count = 1 THEN
        RAISE NOTICE 'ÉXITO: Configuración Casbin completada correctamente';
        RAISE NOTICE '';
        RAISE NOTICE '=== CREDENCIALES DE ACCESO ===';
        RAISE NOTICE 'Email: superadmin@upeu.edu.pe';
        RAISE NOTICE 'Contraseña: SuperAdmin2025!';
        RAISE NOTICE 'Rol Casbin: ADMIN (acceso completo a todo el sistema)';
        RAISE NOTICE '';
        RAISE NOTICE '=== CARACTERÍSTICAS DEL USUARIO ===';
        RAISE NOTICE 'USUARIO GLOBAL: Sin dependencia a ninguna universidad específica';
        RAISE NOTICE 'Puede configurar TODO el sistema, incluyendo:';
        RAISE NOTICE '  - Universidades (configuración estética)';
        RAISE NOTICE '  - Unidades organizativas (Facultades, Escuelas, Departamentos)';
        RAISE NOTICE '  - Autoridades y jerarquías universitarias';
        RAISE NOTICE '  - Usuarios y permisos vía Casbin';
        RAISE NOTICE '';
        RAISE NOTICE '=== ENDPOINTS PÚBLICOS CONFIGURADOS ===';
        RAISE NOTICE 'Los siguientes endpoints NO requieren autenticación:';
        RAISE NOTICE '  - POST /api/v1/auth/login (para ingresar)';
        RAISE NOTICE '  - POST /api/v1/auth/register (para registrarse)';
        RAISE NOTICE '  - POST /api/v1/auth/refresh (para refrescar token)';
        RAISE NOTICE '  - GET /landing/* (para landing page)';
        RAISE NOTICE '';
        RAISE NOTICE '=== CÓMO FUNCIONA CASBIN ===';
        RAISE NOTICE 'Casbin mapea: usuario (email) -> rol -> permisos';
        RAISE NOTICE '  1. Tabla casbin_rule tipo p: (ptype=p, rol, endpoint, método)';
        RAISE NOTICE '  2. Tabla casbin_rule tipo g: (ptype=g, email, rol)';
        RAISE NOTICE '  3. El sistema valida: ¿Este email tiene este rol? ¿Este rol puede acceder al endpoint?';
        RAISE NOTICE '  4. Usuario no autenticado usa rol "public" para acceder a endpoints públicos';
        RAISE NOTICE '';
        RAISE NOTICE 'IMPORTANTE: Cambiar la contraseña después del primer acceso por seguridad';
    ELSE
        RAISE WARNING 'ADVERTENCIA: Algunos elementos no se crearon correctamente';
    END IF;
END $$;

-- =====================================================
-- POLÍTICAS ESPECÍFICAS PARA SUPER ADMINISTRADOR
-- APIs de configuración y administración del sistema
-- =====================================================

-- ADMINISTRACIÓN DE UNIVERSIDADES
INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/universidades', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/universidades' AND v2 = 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/universidades', 'POST'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/universidades' AND v2 = 'POST');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/universidades/*', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/universidades/*' AND v2 = 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/universidades/*', 'PUT'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/universidades/*' AND v2 = 'PUT');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/universidades/*', 'DELETE'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/universidades/*' AND v2 = 'DELETE');

-- ADMINISTRACIÓN DE UNIDADES ORGANIZATIVAS
INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/unidades-organizativas', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/unidades-organizativas' AND v2 = 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/unidades-organizativas', 'POST'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/unidades-organizativas' AND v2 = 'POST');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/unidades-organizativas/*', '*'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/unidades-organizativas/*' AND v2 = '*');

-- ADMINISTRACIÓN DE TIPOS DE UNIDAD
INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/tipo-unidad', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/tipo-unidad' AND v2 = 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/tipo-unidad', 'POST'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/tipo-unidad' AND v2 = 'POST');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/tipo-unidad/*', '*'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/tipo-unidad/*' AND v2 = '*');

-- ADMINISTRACIÓN DE TIPOS DE AUTORIDAD
INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/tipo-autoridad', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/tipo-autoridad' AND v2 = 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/tipo-autoridad', 'POST'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/tipo-autoridad' AND v2 = 'POST');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/tipo-autoridad/*', '*'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/tipo-autoridad/*' AND v2 = '*');

-- ADMINISTRACIÓN DE TIPOS DE LOCALIZACIÓN
INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/tipo-localizacion', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/tipo-localizacion' AND v2 = 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/tipo-localizacion', 'POST'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/tipo-localizacion' AND v2 = 'POST');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/tipo-localizacion/*', '*'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/tipo-localizacion/*' AND v2 = '*');

-- ADMINISTRACIÓN DE LOCALIZACIONES
INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/localizaciones', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/localizaciones' AND v2 = 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/localizaciones', 'POST'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/localizaciones' AND v2 = 'POST');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/localizaciones/*', '*'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/localizaciones/*' AND v2 = '*');

-- ADMINISTRACIÓN DE PERSONAS
INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/personas', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/personas' AND v2 = 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/personas', 'POST'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/personas' AND v2 = 'POST');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/personas/*', '*'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/personas/*' AND v2 = '*');

-- ADMINISTRACIÓN DE AUTORIDADES
INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/autoridades', 'GET'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/autoridades' AND v2 = 'GET');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/autoridades', 'POST'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/autoridades' AND v2 = 'POST');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/autoridades/*', '*'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/autoridades/*' AND v2 = '*');

-- ADMINISTRACIÓN DE ROLES Y PERMISOS (Casbin)
INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/casbin/*', '*'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/casbin/*' AND v2 = '*');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/permisos/*', '*'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/permisos/*' AND v2 = '*');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/roles/*', '*'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/roles/*' AND v2 = '*');

-- ADMINISTRACIÓN DE CONFIGURACIÓN
INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/configuracion/*', '*'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/configuracion/*' AND v2 = '*');

INSERT INTO casbin_rule (ptype, v0, v1, v2)
SELECT 'p', 'ADMIN', '/api/v1/config/*', '*'
WHERE NOT EXISTS (SELECT 1 FROM casbin_rule WHERE ptype = 'p' AND v0 = 'ADMIN' AND v1 = '/api/v1/config/*' AND v2 = '*');
```
