-- =====================================================
-- V020__create_permission_system_tables.sql
-- Sistema completo de permisos escalables con islas/módulos
-- =====================================================

-- =====================================================
-- 1. TABLA: ISLA (Módulos principales)
-- =====================================================
CREATE TABLE IF NOT EXISTS isla (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    icono VARCHAR(50),
    color VARCHAR(20),
    ruta_default VARCHAR(255),
    es_isla_principal BOOLEAN DEFAULT false,
    orden INTEGER DEFAULT 0,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_isla_codigo ON isla(codigo);
CREATE INDEX idx_isla_active ON isla(active);

-- =====================================================
-- 2. TABLA: MODULO (Agrupaciones dentro de cada isla)
-- =====================================================
CREATE TABLE IF NOT EXISTS modulo (
    id BIGSERIAL PRIMARY KEY,
    isla_id BIGINT NOT NULL REFERENCES isla(id) ON DELETE CASCADE,
    codigo VARCHAR(50) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    icono VARCHAR(50),
    orden INTEGER DEFAULT 0,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT uk_modulo_isla_codigo UNIQUE (isla_id, codigo)
);

CREATE INDEX idx_modulo_isla ON modulo(isla_id);
CREATE INDEX idx_modulo_codigo ON modulo(codigo);
CREATE INDEX idx_modulo_active ON modulo(active);

-- =====================================================
-- 3. TABLA: RECURSO (Sidebar targets / Opciones de menú)
-- =====================================================
CREATE TABLE IF NOT EXISTS recurso (
    id BIGSERIAL PRIMARY KEY,
    modulo_id BIGINT NOT NULL REFERENCES modulo(id) ON DELETE CASCADE,
    codigo VARCHAR(50) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    ruta_frontend VARCHAR(255) NOT NULL,
    icono VARCHAR(50),
    orden INTEGER DEFAULT 0,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT uk_recurso_modulo_codigo UNIQUE (modulo_id, codigo)
);

CREATE INDEX idx_recurso_modulo ON recurso(modulo_id);
CREATE INDEX idx_recurso_codigo ON recurso(codigo);
CREATE INDEX idx_recurso_ruta ON recurso(ruta_frontend);
CREATE INDEX idx_recurso_active ON recurso(active);

-- =====================================================
-- 4. TABLA: ACCION (Acciones HTTP sobre recursos)
-- =====================================================
CREATE TABLE IF NOT EXISTS accion (
    id BIGSERIAL PRIMARY KEY,
    recurso_id BIGINT NOT NULL REFERENCES recurso(id) ON DELETE CASCADE,
    codigo VARCHAR(20) NOT NULL, -- GET, POST, PUT, DELETE, CUSTOM
    nombre VARCHAR(50) NOT NULL,
    descripcion TEXT,
    endpoint VARCHAR(255) NOT NULL,
    metodo_http VARCHAR(10) NOT NULL, -- GET, POST, PUT, DELETE, PATCH
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT uk_accion_recurso_codigo UNIQUE (recurso_id, codigo)
);

CREATE INDEX idx_accion_recurso ON accion(recurso_id);
CREATE INDEX idx_accion_codigo ON accion(codigo);
CREATE INDEX idx_accion_endpoint ON accion(endpoint);
CREATE INDEX idx_accion_active ON accion(active);

-- =====================================================
-- 5. TABLA: ROL_ISLA (Roles tienen acceso a islas)
-- =====================================================
CREATE TABLE IF NOT EXISTS rol_isla (
    id BIGSERIAL PRIMARY KEY,
    rol_nombre VARCHAR(50) NOT NULL, -- 'ADMIN', 'PROFESOR', etc. (de Casbin)
    isla_id BIGINT NOT NULL REFERENCES isla(id) ON DELETE CASCADE,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT uk_rol_isla UNIQUE (rol_nombre, isla_id)
);

CREATE INDEX idx_rol_isla_rol ON rol_isla(rol_nombre);
CREATE INDEX idx_rol_isla_isla ON rol_isla(isla_id);
CREATE INDEX idx_rol_isla_active ON rol_isla(active);

-- =====================================================
-- 6. TABLA: ROL_RECURSO (Roles tienen acceso a recursos/sidebar-targets)
-- =====================================================
CREATE TABLE IF NOT EXISTS rol_recurso (
    id BIGSERIAL PRIMARY KEY,
    rol_nombre VARCHAR(50) NOT NULL,
    recurso_id BIGINT NOT NULL REFERENCES recurso(id) ON DELETE CASCADE,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT uk_rol_recurso UNIQUE (rol_nombre, recurso_id)
);

CREATE INDEX idx_rol_recurso_rol ON rol_recurso(rol_nombre);
CREATE INDEX idx_rol_recurso_recurso ON rol_recurso(recurso_id);
CREATE INDEX idx_rol_recurso_active ON rol_recurso(active);

-- =====================================================
-- 7. TABLA: ROL_ACCION (Roles tienen permiso para ejecutar acciones)
-- =====================================================
CREATE TABLE IF NOT EXISTS rol_accion (
    id BIGSERIAL PRIMARY KEY,
    rol_nombre VARCHAR(50) NOT NULL,
    accion_id BIGINT NOT NULL REFERENCES accion(id) ON DELETE CASCADE,
    permitido BOOLEAN DEFAULT true,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT uk_rol_accion UNIQUE (rol_nombre, accion_id)
);

CREATE INDEX idx_rol_accion_rol ON rol_accion(rol_nombre);
CREATE INDEX idx_rol_accion_accion ON rol_accion(accion_id);
CREATE INDEX idx_rol_accion_permitido ON rol_accion(permitido);
CREATE INDEX idx_rol_accion_active ON rol_accion(active);

-- =====================================================
-- 8. TABLA: USUARIO_ISLA (Permisos individuales - islas extra)
-- =====================================================
CREATE TABLE IF NOT EXISTS usuario_isla (
    id BIGSERIAL PRIMARY KEY,
    usuario_email VARCHAR(255) NOT NULL,
    isla_id BIGINT NOT NULL REFERENCES isla(id) ON DELETE CASCADE,
    otorgado_por VARCHAR(255),
    fecha_otorgamiento TIMESTAMP DEFAULT NOW(),
    fecha_expiracion TIMESTAMP,
    razon TEXT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT uk_usuario_isla UNIQUE (usuario_email, isla_id)
);

CREATE INDEX idx_usuario_isla_email ON usuario_isla(usuario_email);
CREATE INDEX idx_usuario_isla_isla ON usuario_isla(isla_id);
CREATE INDEX idx_usuario_isla_expiracion ON usuario_isla(fecha_expiracion);
CREATE INDEX idx_usuario_isla_active ON usuario_isla(active);

-- =====================================================
-- 9. TABLA: USUARIO_RECURSO (Permisos individuales - sidebar-targets extra)
-- =====================================================
CREATE TABLE IF NOT EXISTS usuario_recurso (
    id BIGSERIAL PRIMARY KEY,
    usuario_email VARCHAR(255) NOT NULL,
    recurso_id BIGINT NOT NULL REFERENCES recurso(id) ON DELETE CASCADE,
    otorgado_por VARCHAR(255),
    fecha_otorgamiento TIMESTAMP DEFAULT NOW(),
    fecha_expiracion TIMESTAMP,
    razon TEXT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT uk_usuario_recurso UNIQUE (usuario_email, recurso_id)
);

CREATE INDEX idx_usuario_recurso_email ON usuario_recurso(usuario_email);
CREATE INDEX idx_usuario_recurso_recurso ON usuario_recurso(recurso_id);
CREATE INDEX idx_usuario_recurso_expiracion ON usuario_recurso(fecha_expiracion);
CREATE INDEX idx_usuario_recurso_active ON usuario_recurso(active);

-- =====================================================
-- 10. TABLA: USUARIO_ACCION (Permisos individuales - acciones extra)
-- =====================================================
CREATE TABLE IF NOT EXISTS usuario_accion (
    id BIGSERIAL PRIMARY KEY,
    usuario_email VARCHAR(255) NOT NULL,
    accion_id BIGINT NOT NULL REFERENCES accion(id) ON DELETE CASCADE,
    permitido BOOLEAN DEFAULT true,
    otorgado_por VARCHAR(255),
    fecha_otorgamiento TIMESTAMP DEFAULT NOW(),
    fecha_expiracion TIMESTAMP,
    razon TEXT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT uk_usuario_accion UNIQUE (usuario_email, accion_id)
);

CREATE INDEX idx_usuario_accion_email ON usuario_accion(usuario_email);
CREATE INDEX idx_usuario_accion_accion ON usuario_accion(accion_id);
CREATE INDEX idx_usuario_accion_permitido ON usuario_accion(permitido);
CREATE INDEX idx_usuario_accion_expiracion ON usuario_accion(fecha_expiracion);
CREATE INDEX idx_usuario_accion_active ON usuario_accion(active);

-- =====================================================
-- COMENTARIOS DE TABLAS
-- =====================================================
COMMENT ON TABLE isla IS 'Módulos principales del sistema (ADMIN, PROFESOR, ESTUDIANTE, etc.)';
COMMENT ON TABLE modulo IS 'Agrupaciones de funcionalidades dentro de cada isla';
COMMENT ON TABLE recurso IS 'Opciones del sidebar / Pantallas específicas';
COMMENT ON TABLE accion IS 'Acciones HTTP que se pueden ejecutar sobre cada recurso';
COMMENT ON TABLE rol_isla IS 'Asignación de islas a roles (herencia de Casbin)';
COMMENT ON TABLE rol_recurso IS 'Asignación de recursos/sidebar a roles';
COMMENT ON TABLE rol_accion IS 'Permisos de acción por rol';
COMMENT ON TABLE usuario_isla IS 'Permisos individuales - islas extra para usuarios específicos';
COMMENT ON TABLE usuario_recurso IS 'Permisos individuales - recursos extra para usuarios específicos';
COMMENT ON TABLE usuario_accion IS 'Permisos individuales - acciones extra para usuarios específicos';

