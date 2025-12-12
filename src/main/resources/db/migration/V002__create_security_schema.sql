-- =====================================================
-- MIGRACIÓN V002: MÓDULO DE SEGURIDAD
-- Tablas de autenticación, roles y permisos
-- =====================================================

-- =====================================================
-- TABLAS DE SEGURIDAD Y AUTENTICACIÓN
-- =====================================================

-- Tabla: rol
CREATE TABLE rol (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion TEXT,
    es_sistema BOOLEAN DEFAULT false,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Tabla: permiso
CREATE TABLE permiso (
    id BIGSERIAL PRIMARY KEY,
    nombre_clave VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    modulo VARCHAR(50) NOT NULL,
    recurso VARCHAR(50) NOT NULL,
    accion VARCHAR(50) NOT NULL,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Tabla: rol_permiso (muchos a muchos)
CREATE TABLE rol_permiso (
    id BIGSERIAL PRIMARY KEY,
    rol_id BIGINT NOT NULL,
    permiso_id BIGINT NOT NULL,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_rol_permiso_rol FOREIGN KEY (rol_id) REFERENCES rol(id) ON DELETE CASCADE,
    CONSTRAINT fk_rol_permiso_permiso FOREIGN KEY (permiso_id) REFERENCES permiso(id) ON DELETE CASCADE,
    CONSTRAINT uk_rol_permiso UNIQUE (rol_id, permiso_id)
);

-- Tabla: auth_usuario
CREATE TABLE auth_usuario (
    id BIGSERIAL PRIMARY KEY,
    persona_id BIGINT NOT NULL UNIQUE,
    rol_id BIGINT NOT NULL,
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
    CONSTRAINT fk_auth_usuario_persona FOREIGN KEY (persona_id) REFERENCES persona(id) ON DELETE CASCADE,
    CONSTRAINT fk_auth_usuario_rol FOREIGN KEY (rol_id) REFERENCES rol(id)
);

-- Tabla: refresh_tokens (para JWT)
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    auth_usuario_id BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL UNIQUE,
    fecha_expiracion TIMESTAMP NOT NULL,
    revocado BOOLEAN DEFAULT false,
    fecha_revocacion TIMESTAMP,
    ip_origen VARCHAR(45),
    user_agent VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_refresh_token_auth_usuario FOREIGN KEY (auth_usuario_id) REFERENCES auth_usuario(id) ON DELETE CASCADE
);

-- =====================================================
-- ÍNDICES PARA OPTIMIZACIÓN
-- =====================================================

CREATE INDEX idx_auth_usuario_persona ON auth_usuario(persona_id);
CREATE INDEX idx_auth_usuario_rol ON auth_usuario(rol_id);
CREATE INDEX idx_rol_permiso_rol ON rol_permiso(rol_id);
CREATE INDEX idx_rol_permiso_permiso ON rol_permiso(permiso_id);
CREATE INDEX idx_refresh_token_usuario ON refresh_tokens(auth_usuario_id);
CREATE INDEX idx_refresh_token_expiracion ON refresh_tokens(fecha_expiracion);
CREATE INDEX idx_permiso_modulo_recurso ON permiso(modulo, recurso, accion);

-- =====================================================
-- COMENTARIOS DE TABLAS
-- =====================================================

COMMENT ON TABLE rol IS 'Roles de usuario para control de acceso basado en roles (RBAC)';
COMMENT ON TABLE permiso IS 'Permisos granulares del sistema organizados por módulo';
COMMENT ON TABLE rol_permiso IS 'Relación muchos a muchos entre roles y permisos';
COMMENT ON TABLE auth_usuario IS 'Usuarios autenticados del sistema con credenciales';
COMMENT ON TABLE refresh_tokens IS 'Tokens de refresco para autenticación JWT';

COMMENT ON COLUMN rol.es_sistema IS 'Indica si es un rol predefinido del sistema (no se puede eliminar)';
COMMENT ON COLUMN auth_usuario.intentos_fallidos IS 'Contador de intentos fallidos de login (se resetea en login exitoso)';
COMMENT ON COLUMN auth_usuario.fecha_bloqueo IS 'Fecha de bloqueo del usuario por exceso de intentos fallidos';
COMMENT ON COLUMN auth_usuario.requiere_cambio_password IS 'Indica si el usuario debe cambiar su password en el próximo acceso';
COMMENT ON COLUMN permiso.nombre_clave IS 'Identificador único del permiso en formato RECURSO_ACCION';
COMMENT ON COLUMN permiso.modulo IS 'Módulo del sistema (SEGURIDAD, CURRICULUM, MATRICULA, etc.)';
COMMENT ON COLUMN permiso.recurso IS 'Recurso sobre el cual se aplica el permiso (USUARIOS, SILABOS, etc.)';
COMMENT ON COLUMN permiso.accion IS 'Acción permitida (CREAR, LEER, ACTUALIZAR, ELIMINAR, APROBAR, etc.)';
