-- =====================================================
-- MIGRACIÓN V001: ESQUEMA BASE DEL SISTEMA
-- Tablas core del sistema académico
-- =====================================================

-- =====================================================
-- MÓDULO: CORE - Estructura organizacional
-- =====================================================

-- Tabla: tipo_unidad
CREATE TABLE tipo_unidad (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    nivel_jerarquia INTEGER NOT NULL,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Tabla: tipo_localizacion (recursiva)
CREATE TABLE tipo_localizacion (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    tipo_padre_id BIGINT,
    nivel_jerarquia INTEGER NOT NULL,
    permite_asignacion BOOLEAN DEFAULT true,
    codigo VARCHAR(20),
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_tipo_localizacion_padre FOREIGN KEY (tipo_padre_id) REFERENCES tipo_localizacion(id)
);

-- Tabla: localizacion (recursiva)
CREATE TABLE localizacion (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    direccion TEXT,
    localizacion_padre_id BIGINT,
    tipo_localizacion_id BIGINT NOT NULL,
    capacidad INTEGER,
    estado VARCHAR(20),
    departamento VARCHAR(100),
    provincia VARCHAR(100),
    distrito VARCHAR(100),
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_localizacion_padre FOREIGN KEY (localizacion_padre_id) REFERENCES localizacion(id),
    CONSTRAINT fk_localizacion_tipo FOREIGN KEY (tipo_localizacion_id) REFERENCES tipo_localizacion(id)
);

-- Tabla: unidad_organizativa (recursiva)
CREATE TABLE unidad_organizativa (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    codigo VARCHAR(50),
    tipo_unidad_id BIGINT NOT NULL,
    unidad_padre_id BIGINT,
    localizacion_id BIGINT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_unidad_tipo FOREIGN KEY (tipo_unidad_id) REFERENCES tipo_unidad(id),
    CONSTRAINT fk_unidad_padre FOREIGN KEY (unidad_padre_id) REFERENCES unidad_organizativa(id),
    CONSTRAINT fk_unidad_localizacion FOREIGN KEY (localizacion_id) REFERENCES localizacion(id)
);

-- =====================================================
-- MÓDULO: PEOPLE - Gestión de personas
-- =====================================================

-- Tabla: persona
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

-- Tabla: empleado
CREATE TABLE empleado (
    id BIGSERIAL PRIMARY KEY,
    persona_id BIGINT NOT NULL UNIQUE,
    codigo_empleado VARCHAR(20) NOT NULL UNIQUE,
    fecha_ingreso DATE,
    fecha_cese DATE,
    cargo VARCHAR(100),
    tipo_contrato VARCHAR(50),
    regimen_laboral VARCHAR(50),
    salario NUMERIC(10, 2),
    estado_laboral VARCHAR(20),
    unidad_organizativa_id BIGINT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_empleado_persona FOREIGN KEY (persona_id) REFERENCES persona(id),
    CONSTRAINT fk_empleado_unidad FOREIGN KEY (unidad_organizativa_id) REFERENCES unidad_organizativa(id)
);

-- Tabla: tipo_autoridad
CREATE TABLE tipo_autoridad (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    nivel_jerarquia INTEGER NOT NULL,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Tabla: autoridad
CREATE TABLE autoridad (
    id BIGSERIAL PRIMARY KEY,
    persona_id BIGINT NOT NULL,
    tipo_autoridad_id BIGINT NOT NULL,
    unidad_organizativa_id BIGINT,
    programa_academico_id BIGINT,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE,
    es_vigente BOOLEAN,
    resolucion_designacion VARCHAR(100),
    observaciones TEXT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_autoridad_persona FOREIGN KEY (persona_id) REFERENCES persona(id),
    CONSTRAINT fk_autoridad_tipo FOREIGN KEY (tipo_autoridad_id) REFERENCES tipo_autoridad(id),
    CONSTRAINT fk_autoridad_unidad FOREIGN KEY (unidad_organizativa_id) REFERENCES unidad_organizativa(id)
);

-- Tabla: profesor
CREATE TABLE profesor (
    id BIGSERIAL PRIMARY KEY,
    empleado_id BIGINT NOT NULL UNIQUE,
    unidad_id BIGINT,
    grado_academico VARCHAR(100),
    especialidad VARCHAR(200),
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_profesor_empleado FOREIGN KEY (empleado_id) REFERENCES empleado(id),
    CONSTRAINT fk_profesor_unidad FOREIGN KEY (unidad_id) REFERENCES unidad_organizativa(id)
);

-- =====================================================
-- MÓDULO: SISTEMA - Auditoría y logs
-- =====================================================

-- Tabla: auditoria_general
CREATE TABLE auditoria_general (
    id BIGSERIAL PRIMARY KEY,
    tabla_afectada VARCHAR(100) NOT NULL,
    registro_id BIGINT NOT NULL,
    accion VARCHAR(20) NOT NULL,
    usuario VARCHAR(100) NOT NULL,
    fecha_hora TIMESTAMP NOT NULL DEFAULT NOW(),
    valores_anteriores JSONB,
    valores_nuevos JSONB,
    ip_origen VARCHAR(45),
    navegador VARCHAR(200),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Tabla: sistema_log
CREATE TABLE sistema_log (
    id BIGSERIAL PRIMARY KEY,
    nivel VARCHAR(20) NOT NULL,
    modulo VARCHAR(50) NOT NULL,
    mensaje TEXT NOT NULL,
    usuario VARCHAR(100),
    contexto JSONB,
    ip_origen VARCHAR(45),
    fecha_hora TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Tabla: sistema_backup
CREATE TABLE sistema_backup (
    id BIGSERIAL PRIMARY KEY,
    tipo VARCHAR(20) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP,
    tamano_gb NUMERIC(10,2),
    registros_totales BIGINT,
    duracion_minutos INTEGER,
    archivo_destino VARCHAR(500),
    usuario_solicitante VARCHAR(100),
    error_mensaje TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- =====================================================
-- ÍNDICES PARA OPTIMIZACIÓN
-- =====================================================

-- Índices para búsquedas frecuentes
CREATE INDEX idx_persona_documento ON persona(numero_documento);
CREATE INDEX idx_persona_email ON persona(email);
CREATE INDEX idx_empleado_codigo ON empleado(codigo_empleado);
CREATE INDEX idx_auditoria_tabla_registro ON auditoria_general(tabla_afectada, registro_id);
CREATE INDEX idx_auditoria_usuario_fecha ON auditoria_general(usuario, fecha_hora DESC);
CREATE INDEX idx_log_nivel_fecha ON sistema_log(nivel, fecha_hora DESC);
CREATE INDEX idx_log_modulo_fecha ON sistema_log(modulo, fecha_hora DESC);
CREATE INDEX idx_backup_tipo_fecha ON sistema_backup(tipo, fecha_inicio DESC);
CREATE INDEX idx_localizacion_tipo ON localizacion(tipo_localizacion_id);
CREATE INDEX idx_unidad_tipo ON unidad_organizativa(tipo_unidad_id);

-- =====================================================
-- COMENTARIOS DE TABLAS
-- =====================================================

COMMENT ON TABLE persona IS 'Información base de todas las personas del sistema';
COMMENT ON TABLE empleado IS 'Datos laborales de empleados de la institución';
COMMENT ON TABLE profesor IS 'Profesores con capacidad de dictar cursos';
COMMENT ON TABLE autoridad IS 'Autoridades designadas en unidades organizativas';
COMMENT ON TABLE auditoria_general IS 'Registro completo de cambios en el sistema';
COMMENT ON TABLE sistema_log IS 'Logs de eventos y errores del sistema';
COMMENT ON TABLE sistema_backup IS 'Historial de respaldos de base de datos';
