-- ============================================================================
-- SISTEMA DE GESTIÓN UNIVERSITARIA - DDL COMPLETO
-- ============================================================================
-- Generado automáticamente
-- Total de tablas: 33
-- ============================================================================


-- ============================================================================
-- MÓDULO: ACADEMIC
-- ============================================================================

-- asistencia_alumno
-- Control de asistencia a clases.

CREATE TABLE asistencia_alumno (
    fecha_clase DATE NOT NULL,
    estado VARCHAR(20) NOT NULL,
    observaciones VARCHAR,
    minutos_tardanza INTEGER,
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

ALTER TABLE asistencia_alumno ADD CONSTRAINT uk_asistencia_alumno_1 UNIQUE (estudiante_id, horario_id, fecha_clase);

-- autoridad
-- Cargos directivos (rector, decano, director).

CREATE TABLE autoridad (
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE,
    activo BOOLEAN,
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

-- curso
-- Asignaturas del catálogo académico.

CREATE TABLE curso (
    codigo_curso VARCHAR(20) NOT NULL,
    nombre VARCHAR(200) NOT NULL,
    descripcion VARCHAR,
    horas_teoricas INTEGER,
    horas_practicas INTEGER,
    horas_semanales INTEGER,
    tipo_curso VARCHAR(50),
    area_curricular VARCHAR(100),
    silabo_url VARCHAR(255),
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

ALTER TABLE curso ADD CONSTRAINT uk_curso_1 UNIQUE (codigo_curso, universidad_id);

-- curso_ofertado
-- Cursos programados para un periodo académico.

CREATE TABLE curso_ofertado (
    codigo_seccion VARCHAR(20) NOT NULL,
    capacidad_maxima INTEGER NOT NULL,
    vacantes_disponibles INTEGER NOT NULL,
    modalidad VARCHAR(50),
    estado VARCHAR(20),
    observaciones VARCHAR(500),
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

ALTER TABLE curso_ofertado ADD CONSTRAINT uk_curso_ofertado_1 UNIQUE (codigo_seccion, periodo_academico_id, universidad_id);

-- empleado
-- Personal administrativo y de servicios.

CREATE TABLE empleado (
    codigo_empleado VARCHAR(20) NOT NULL,
    fecha_ingreso DATE,
    fecha_cese DATE,
    cargo VARCHAR(100),
    tipo_contrato VARCHAR(50),
    regimen_laboral VARCHAR(50),
    salario DECIMAL(10,2),
    estado_laboral VARCHAR(20),
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

ALTER TABLE empleado ADD CONSTRAINT uk_empleado_1 UNIQUE (codigo_empleado, universidad_id);
ALTER TABLE empleado ADD CONSTRAINT uk_empleado_2 UNIQUE (persona_id, universidad_id);

-- estudiante
-- Gestiona datos académicos de estudiantes matriculados.

CREATE TABLE estudiante (
    codigo_estudiante VARCHAR(20) NOT NULL UNIQUE,
    fecha_ingreso DATE,
    ciclo_actual INTEGER,
    creditos_aprobados INTEGER,
    modalidad_ingreso VARCHAR(50),
    estado_academico VARCHAR(20),
    tipo_estudiante VARCHAR(20),
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

ALTER TABLE estudiante ADD CONSTRAINT uk_estudiante_1 UNIQUE (codigo_estudiante, universidad_id);
ALTER TABLE estudiante ADD CONSTRAINT uk_estudiante_2 UNIQUE (persona_id, universidad_id);

-- evaluacion_criterio
-- Criterios de evaluación (exámenes, prácticas).

CREATE TABLE evaluacion_criterio (
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500),
    peso INTEGER NOT NULL,
    tipo_evaluacion VARCHAR(50),
    orden INTEGER,
    estado VARCHAR(20),
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

ALTER TABLE evaluacion_criterio ADD CONSTRAINT uk_evaluacion_criterio_1 UNIQUE (curso_ofertado_id, nombre, universidad_id);

-- evaluacion_nota
-- Calificaciones de estudiantes.

CREATE TABLE evaluacion_nota (
    nota DECIMAL(5,2),
    nota_recuperacion DECIMAL(5,2),
    nota_final DECIMAL(5,2),
    observacion VARCHAR(500),
    fecha_evaluacion TIMESTAMP,
    fecha_calificacion TIMESTAMP,
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

ALTER TABLE evaluacion_nota ADD CONSTRAINT uk_evaluacion_nota_1 UNIQUE (matricula_id, criterio_id);

-- horario
-- Programación de clases (día, hora, aula).

CREATE TABLE horario (
    dia_semana INTEGER NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    tipo_sesion VARCHAR(20),
    observaciones VARCHAR(500),
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

ALTER TABLE horario ADD CONSTRAINT uk_horario_1 UNIQUE (curso_ofertado_id, dia_semana, hora_inicio);

-- localizacion
-- Espacios físicos (aulas, laboratorios).

CREATE TABLE localizacion (
    nombre VARCHAR(200) NOT NULL,
    codigo VARCHAR(20),
    direccion VARCHAR(500),
    telefono VARCHAR(20),
    email VARCHAR(100),
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

ALTER TABLE localizacion ADD CONSTRAINT uk_localizacion_1 UNIQUE (codigo, universidad_id);

-- matricula
-- Inscripción de estudiantes en cursos.

CREATE TABLE matricula (
    creditos_matriculados INTEGER,
    fecha_retiro DATE,
    nota_final DECIMAL(5,2),
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

ALTER TABLE matricula ADD CONSTRAINT uk_matricula_1 UNIQUE (estudiante_id, curso_ofertado_id);

-- periodo_academico
-- Semestres/ciclos académicos.

CREATE TABLE periodo_academico (
    codigo_periodo VARCHAR(20) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    anio INTEGER NOT NULL,
    tipo_periodo VARCHAR(50),
    numero_periodo INTEGER,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    fecha_inicio_matricula DATE,
    fecha_fin_matricula DATE,
    fecha_inicio_clases DATE,
    fecha_fin_clases DATE,
    estado VARCHAR(20),
    descripcion VARCHAR(500),
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

ALTER TABLE periodo_academico ADD CONSTRAINT uk_periodo_academico_1 UNIQUE (codigo_periodo, universidad_id);

-- persona
-- Registro centralizado de personas (estudiantes, profesores, empleados).

CREATE TABLE persona (
    nombres VARCHAR(100) NOT NULL,
    apellido_paterno VARCHAR(50) NOT NULL,
    apellido_materno VARCHAR(50),
    tipo_documento VARCHAR(20),
    numero_documento VARCHAR(20),
    fecha_nacimiento DATE,
    genero VARCHAR(20),
    estado_civil VARCHAR(20),
    direccion VARCHAR(255),
    telefono VARCHAR(15),
    celular VARCHAR(15),
    email VARCHAR(100) UNIQUE,
    foto_url VARCHAR(255),
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

ALTER TABLE persona ADD CONSTRAINT uk_persona_1 UNIQUE (numero_documento, universidad_id);
ALTER TABLE persona ADD CONSTRAINT uk_persona_2 UNIQUE (email, universidad_id);

-- plan_academico
-- Planes de estudio por programa y versión.

CREATE TABLE plan_academico (
    codigo VARCHAR(20) NOT NULL,
    version VARCHAR(10),
    nombre VARCHAR(200) NOT NULL,
    fecha_aprobacion DATE,
    fecha_vigencia_inicio DATE,
    fecha_vigencia_fin DATE,
    creditos_totales INTEGER,
    creditos_obligatorios INTEGER,
    creditos_electivos INTEGER,
    duracion_semestres INTEGER,
    estado VARCHAR(20),
    creditos_maximos_por_ciclo INTEGER,
    creditos_minimos_tiempo_completo INTEGER,
    duracion_ciclo_meses INTEGER,
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

ALTER TABLE plan_academico ADD CONSTRAINT uk_plan_academico_1 UNIQUE (codigo, universidad_id);

-- plan_curso
-- Cursos que pertenecen a un plan académico.

CREATE TABLE plan_curso (
    creditos INTEGER NOT NULL,
    ciclo INTEGER NOT NULL,
    tipo_curso VARCHAR(20),
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

ALTER TABLE plan_curso ADD CONSTRAINT uk_plan_curso_1 UNIQUE (plan_academico_id, curso_id, universidad_id);

-- profesor
-- Información de docentes y sus datos académicos.

CREATE TABLE profesor (
    grado_academico VARCHAR(50),
    especialidad VARCHAR(100),
    categoria_docente VARCHAR(50),
    condicion_docente VARCHAR(50),
    dedicacion VARCHAR(50),
    codigo_orcid VARCHAR(50),
    codigo_renacyt VARCHAR(50),
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

ALTER TABLE profesor ADD CONSTRAINT uk_profesor_1 UNIQUE (persona_id, universidad_id);

-- programa_academico
-- Carreras profesionales ofrecidas por la universidad.

CREATE TABLE programa_academico (
    nombre VARCHAR(200) NOT NULL,
    codigo VARCHAR(20) NOT NULL,
    nivel_academico VARCHAR(50),
    modalidad VARCHAR(50),
    duracion_anios INTEGER,
    duracion_semestres INTEGER,
    creditos_totales INTEGER,
    titulo_otorgado VARCHAR(200),
    grado_academico VARCHAR(200),
    cupo_maximo_anual INTEGER,
    nota_minima_ingreso DECIMAL(4,2),
    programa_padre_id BIGINT,
    fecha_creacion_programa DATE,
    fecha_ultima_modificacion_plan DATE,
    estado VARCHAR(20),
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

ALTER TABLE programa_academico ADD CONSTRAINT uk_programa_academico_1 UNIQUE (codigo, universidad_id);

-- requisito_curso
-- Prerequisitos entre cursos.

CREATE TABLE requisito_curso (
    tipo_requisito VARCHAR(50) NOT NULL,
    nota_minima_requerida INTEGER,
    observacion VARCHAR(500),
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

ALTER TABLE requisito_curso ADD CONSTRAINT uk_requisito_curso_1 UNIQUE (curso_id, curso_requisito_id, tipo_requisito, universidad_id);

-- tipo_autoridad
-- Catálogo de tipos de autoridades.

CREATE TABLE tipo_autoridad (
    nombre VARCHAR(100) NOT NULL,
    codigo VARCHAR(20),
    nivel_jerarquia INTEGER,
    descripcion VARCHAR(255),
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

ALTER TABLE tipo_autoridad ADD CONSTRAINT uk_tipo_autoridad_1 UNIQUE (nombre, universidad_id);

-- tipo_localizacion
-- Catálogo de tipos de localizaciones.

CREATE TABLE tipo_localizacion (
    nombre VARCHAR(100) NOT NULL,
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

-- tipo_unidad
-- Catálogo de tipos de unidades organizativas.

CREATE TABLE tipo_unidad (
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    nivel INTEGER,
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

ALTER TABLE tipo_unidad ADD CONSTRAINT uk_tipo_unidad_1 UNIQUE (nombre, universidad_id);

-- unidad_organizativa
-- Estructura organizacional (facultades, escuelas).

CREATE TABLE unidad_organizativa (
    nombre VARCHAR(200) NOT NULL,
    codigo VARCHAR(20),
    sigla VARCHAR(20),
    descripcion VARCHAR,
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

ALTER TABLE unidad_organizativa ADD CONSTRAINT uk_unidad_organizativa_1 UNIQUE (codigo, universidad_id);
ALTER TABLE unidad_organizativa ADD CONSTRAINT uk_unidad_organizativa_2 UNIQUE (nombre, universidad_id);

-- universidades
-- Gestiona información de universidades/instituciones. Central para multitenancy.

CREATE TABLE universidades (
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(255) NOT NULL,
    dominio VARCHAR(50) UNIQUE,
    ruc VARCHAR(11) NOT NULL UNIQUE,
    tipo VARCHAR(50) NOT NULL,
    website VARCHAR(255),
    logo_url VARCHAR(500),
    zona_horaria VARCHAR(50),
    locale VARCHAR(20),
    configuracion VARCHAR,
    plan VARCHAR(20),
    estado VARCHAR(20),
    max_estudiantes INTEGER,
    max_docentes INTEGER,
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);


-- ============================================================================
-- MÓDULO: CATALOG
-- ============================================================================

-- categories
-- Categorías del catálogo general.

CREATE TABLE categories (
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);


-- ============================================================================
-- MÓDULO: FINANCE
-- ============================================================================

-- cuenta_corriente_alumno
-- Estado de cuenta de estudiantes.

CREATE TABLE cuenta_corriente_alumno (
    monto DECIMAL(10,2) NOT NULL,
    monto_pendiente DECIMAL(10,2),
    concepto VARCHAR(255) NOT NULL,
    tipo_cargo VARCHAR(50),
    fecha_vencimiento DATE,
    fecha_emision DATE NOT NULL,
    estado VARCHAR(20),
    periodo_academico VARCHAR(20),
    numero_cuota INTEGER,
    observaciones VARCHAR(500),
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

CREATE INDEX idx_cta_cte_universidad_estudiante ON cuenta_corriente_alumno (universidad_id, estudiante_id);
CREATE INDEX idx_cta_cte_estado_vencimiento ON cuenta_corriente_alumno (estado, fecha_vencimiento);
CREATE INDEX idx_cta_cte_periodo_tipo ON cuenta_corriente_alumno (periodo_academico, tipo_cargo);

-- pago
-- Registro de pagos realizados.

CREATE TABLE pago (
    numero_recibo VARCHAR(50) NOT NULL,
    monto_pagado DECIMAL(10,2) NOT NULL,
    monto_pendiente_aplicar DECIMAL(10,2),
    fecha_pago TIMESTAMP NOT NULL,
    metodo_pago VARCHAR(50),
    referencia_pago VARCHAR(100),
    banco VARCHAR(100),
    cajero VARCHAR(100),
    estado VARCHAR(20),
    observaciones VARCHAR(500),
    fecha_anulacion TIMESTAMP,
    motivo_anulacion VARCHAR(500),
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

CREATE INDEX idx_pago_universidad_estudiante ON pago (universidad_id, estudiante_id);
CREATE INDEX idx_pago_fecha_estado ON pago (fecha_pago, estado);
CREATE INDEX idx_pago_numero_recibo ON pago (numero_recibo);

-- pago_detalle_deuda
-- Detalle de aplicación de pagos a deudas.

CREATE TABLE pago_detalle_deuda (
    monto_aplicado DECIMAL(10,2) NOT NULL,
    fecha_aplicacion TIMESTAMP NOT NULL,
    aplicado_por VARCHAR(100),
    observaciones VARCHAR(500),
    estado VARCHAR(20),
    fecha_reversion TIMESTAMP,
    motivo_reversion VARCHAR(500),
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

CREATE INDEX idx_pago_detalle_pago ON pago_detalle_deuda (pago_id);
CREATE INDEX idx_pago_detalle_deuda ON pago_detalle_deuda (deuda_id);
CREATE INDEX idx_pago_detalle_estado ON pago_detalle_deuda (estado, fecha_aplicacion);


-- ============================================================================
-- MÓDULO: SECURITY
-- ============================================================================

-- auth_usuario
-- Usuarios del sistema (autenticación).

CREATE TABLE auth_usuario (
    password_hash VARCHAR(255) NOT NULL,
    ultimo_acceso TIMESTAMP,
    fecha_bloqueo TIMESTAMP,
    fecha_ultimo_cambio_password TIMESTAMP,
    token_recuperacion VARCHAR(255),
    fecha_expiracion_token TIMESTAMP,
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

ALTER TABLE auth_usuario ADD CONSTRAINT uk_auth_usuario_1 UNIQUE (universidad_id, persona_id);

-- permiso
-- Permisos granulares del sistema.

CREATE TABLE permiso (
    nombre_clave VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    modulo VARCHAR(50),
    recurso VARCHAR(100),
    accion VARCHAR(50),
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

-- refresh_tokens
-- Tokens de refresco JWT.

CREATE TABLE refresh_tokens (
    token VARCHAR(1000) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

-- rol
-- Roles de acceso (ADMIN, DOCENTE, ESTUDIANTE).

CREATE TABLE rol (
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

ALTER TABLE rol ADD CONSTRAINT uk_rol_1 UNIQUE (nombre, universidad_id);

-- rol_permiso
-- Asignación de permisos a roles.

CREATE TABLE rol_permiso (
    restriccion VARCHAR(500),
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);

ALTER TABLE rol_permiso ADD CONSTRAINT uk_rol_permiso_1 UNIQUE (rol_id, permiso_id);

-- users

CREATE TABLE users (
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(15),
    last_login TIMESTAMP,
    fecha_bloqueo TIMESTAMP,
    token_recuperacion VARCHAR(255),
    fecha_expiracion_token TIMESTAMP,
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    universidad_id BIGINT
);


-- ============================================================================
-- FOREIGN KEYS
-- ============================================================================

ALTER TABLE asistencia_alumno ADD CONSTRAINT fk_asistencia_alumno_universidad_id FOREIGN KEY (universidad_id) REFERENCES universidad(id);
ALTER TABLE asistencia_alumno ADD CONSTRAINT fk_asistencia_alumno_estudiante_id FOREIGN KEY (estudiante_id) REFERENCES estudiante(id);
ALTER TABLE asistencia_alumno ADD CONSTRAINT fk_asistencia_alumno_horario_id FOREIGN KEY (horario_id) REFERENCES horario(id);
ALTER TABLE asistencia_alumno ADD CONSTRAINT fk_asistencia_alumno_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE autoridad ADD CONSTRAINT fk_autoridad_universidad_id FOREIGN KEY (universidad_id) REFERENCES universidad(id);
ALTER TABLE autoridad ADD CONSTRAINT fk_autoridad_persona_id FOREIGN KEY (persona_id) REFERENCES persona(id);
ALTER TABLE autoridad ADD CONSTRAINT fk_autoridad_tipo_autoridad_id FOREIGN KEY (tipo_autoridad_id) REFERENCES tipo_autoridad(id);
ALTER TABLE autoridad ADD CONSTRAINT fk_autoridad_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE curso ADD CONSTRAINT fk_curso_universidad_id FOREIGN KEY (universidad_id) REFERENCES universidad(id);
ALTER TABLE curso ADD CONSTRAINT fk_curso_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE curso_ofertado ADD CONSTRAINT fk_curso_ofertado_universidad_id FOREIGN KEY (universidad_id) REFERENCES universidad(id);
ALTER TABLE curso_ofertado ADD CONSTRAINT fk_curso_ofertado_plan_curso_id FOREIGN KEY (plan_curso_id) REFERENCES plan_curso(id);
ALTER TABLE curso_ofertado ADD CONSTRAINT fk_curso_ofertado_periodo_academico_id FOREIGN KEY (periodo_academico_id) REFERENCES periodo_academico(id);
ALTER TABLE curso_ofertado ADD CONSTRAINT fk_curso_ofertado_profesor_id FOREIGN KEY (profesor_id) REFERENCES profesor(id);
ALTER TABLE curso_ofertado ADD CONSTRAINT fk_curso_ofertado_localizacion_id FOREIGN KEY (localizacion_id) REFERENCES localizacion(id);
ALTER TABLE curso_ofertado ADD CONSTRAINT fk_curso_ofertado_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE empleado ADD CONSTRAINT fk_empleado_persona_id FOREIGN KEY (persona_id) REFERENCES persona(id);
ALTER TABLE empleado ADD CONSTRAINT fk_empleado_universidad_id FOREIGN KEY (universidad_id) REFERENCES universidad(id);
ALTER TABLE empleado ADD CONSTRAINT fk_empleado_unidad_organizativa_id FOREIGN KEY (unidad_organizativa_id) REFERENCES unidad_organizativa(id);
ALTER TABLE empleado ADD CONSTRAINT fk_empleado_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE estudiante ADD CONSTRAINT fk_estudiante_persona_id FOREIGN KEY (persona_id) REFERENCES persona(id);
ALTER TABLE estudiante ADD CONSTRAINT fk_estudiante_universidad_id FOREIGN KEY (universidad_id) REFERENCES universidad(id);
ALTER TABLE estudiante ADD CONSTRAINT fk_estudiante_programa_id FOREIGN KEY (programa_id) REFERENCES programa_academico(id);
ALTER TABLE estudiante ADD CONSTRAINT fk_estudiante_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE evaluacion_criterio ADD CONSTRAINT fk_evaluacion_criterio_universidad_id FOREIGN KEY (universidad_id) REFERENCES universidad(id);
ALTER TABLE evaluacion_criterio ADD CONSTRAINT fk_evaluacion_criterio_curso_ofertado_id FOREIGN KEY (curso_ofertado_id) REFERENCES curso_ofertado(id);
ALTER TABLE evaluacion_criterio ADD CONSTRAINT fk_evaluacion_criterio_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE evaluacion_nota ADD CONSTRAINT fk_evaluacion_nota_matricula_id FOREIGN KEY (matricula_id) REFERENCES matricula(id);
ALTER TABLE evaluacion_nota ADD CONSTRAINT fk_evaluacion_nota_criterio_id FOREIGN KEY (criterio_id) REFERENCES evaluacion_criterio(id);
ALTER TABLE evaluacion_nota ADD CONSTRAINT fk_evaluacion_nota_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE horario ADD CONSTRAINT fk_horario_universidad_id FOREIGN KEY (universidad_id) REFERENCES universidad(id);
ALTER TABLE horario ADD CONSTRAINT fk_horario_curso_ofertado_id FOREIGN KEY (curso_ofertado_id) REFERENCES curso_ofertado(id);
ALTER TABLE horario ADD CONSTRAINT fk_horario_localizacion_id FOREIGN KEY (localizacion_id) REFERENCES localizacion(id);
ALTER TABLE horario ADD CONSTRAINT fk_horario_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE localizacion ADD CONSTRAINT fk_localizacion_universidad_id FOREIGN KEY (universidad_id) REFERENCES universidad(id);
ALTER TABLE localizacion ADD CONSTRAINT fk_localizacion_tipo_localizacion_id FOREIGN KEY (tipo_localizacion_id) REFERENCES tipo_localizacion(id);
ALTER TABLE localizacion ADD CONSTRAINT fk_localizacion_localizacion_padre_id FOREIGN KEY (localizacion_padre_id) REFERENCES localizacion(id);
ALTER TABLE localizacion ADD CONSTRAINT fk_localizacion_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE matricula ADD CONSTRAINT fk_matricula_universidad_id FOREIGN KEY (universidad_id) REFERENCES universidad(id);
ALTER TABLE matricula ADD CONSTRAINT fk_matricula_estudiante_id FOREIGN KEY (estudiante_id) REFERENCES estudiante(id);
ALTER TABLE matricula ADD CONSTRAINT fk_matricula_curso_ofertado_id FOREIGN KEY (curso_ofertado_id) REFERENCES curso_ofertado(id);
ALTER TABLE matricula ADD CONSTRAINT fk_matricula_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE periodo_academico ADD CONSTRAINT fk_periodo_academico_universidad_id FOREIGN KEY (universidad_id) REFERENCES universidad(id);
ALTER TABLE periodo_academico ADD CONSTRAINT fk_periodo_academico_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE persona ADD CONSTRAINT fk_persona_universidad_id FOREIGN KEY (universidad_id) REFERENCES universidad(id);
ALTER TABLE persona ADD CONSTRAINT fk_persona_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE plan_academico ADD CONSTRAINT fk_plan_academico_universidad_id FOREIGN KEY (universidad_id) REFERENCES universidad(id);
ALTER TABLE plan_academico ADD CONSTRAINT fk_plan_academico_programa_academico_id FOREIGN KEY (programa_academico_id) REFERENCES programa_academico(id);
ALTER TABLE plan_academico ADD CONSTRAINT fk_plan_academico_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE plan_curso ADD CONSTRAINT fk_plan_curso_universidad_id FOREIGN KEY (universidad_id) REFERENCES universidad(id);
ALTER TABLE plan_curso ADD CONSTRAINT fk_plan_curso_plan_academico_id FOREIGN KEY (plan_academico_id) REFERENCES plan_academico(id);
ALTER TABLE plan_curso ADD CONSTRAINT fk_plan_curso_curso_id FOREIGN KEY (curso_id) REFERENCES curso(id);
ALTER TABLE plan_curso ADD CONSTRAINT fk_plan_curso_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE profesor ADD CONSTRAINT fk_profesor_persona_id FOREIGN KEY (persona_id) REFERENCES persona(id);
ALTER TABLE profesor ADD CONSTRAINT fk_profesor_universidad_id FOREIGN KEY (universidad_id) REFERENCES universidad(id);
ALTER TABLE profesor ADD CONSTRAINT fk_profesor_unidad_id FOREIGN KEY (unidad_id) REFERENCES unidad_organizativa(id);
ALTER TABLE profesor ADD CONSTRAINT fk_profesor_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE programa_academico ADD CONSTRAINT fk_programa_academico_universidad_id FOREIGN KEY (universidad_id) REFERENCES universidad(id);
ALTER TABLE programa_academico ADD CONSTRAINT fk_programa_academico_unidad_organizativa_id FOREIGN KEY (unidad_organizativa_id) REFERENCES unidad_organizativa(id);
ALTER TABLE programa_academico ADD CONSTRAINT fk_programa_academico_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE requisito_curso ADD CONSTRAINT fk_requisito_curso_universidad_id FOREIGN KEY (universidad_id) REFERENCES universidad(id);
ALTER TABLE requisito_curso ADD CONSTRAINT fk_requisito_curso_curso_id FOREIGN KEY (curso_id) REFERENCES curso(id);
ALTER TABLE requisito_curso ADD CONSTRAINT fk_requisito_curso_curso_requisito_id FOREIGN KEY (curso_requisito_id) REFERENCES curso(id);
ALTER TABLE requisito_curso ADD CONSTRAINT fk_requisito_curso_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE tipo_autoridad ADD CONSTRAINT fk_tipo_autoridad_universidad_id FOREIGN KEY (universidad_id) REFERENCES universidad(id);
ALTER TABLE tipo_autoridad ADD CONSTRAINT fk_tipo_autoridad_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE tipo_localizacion ADD CONSTRAINT fk_tipo_localizacion_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE tipo_unidad ADD CONSTRAINT fk_tipo_unidad_universidad_id FOREIGN KEY (universidad_id) REFERENCES universidad(id);
ALTER TABLE tipo_unidad ADD CONSTRAINT fk_tipo_unidad_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE unidad_organizativa ADD CONSTRAINT fk_unidad_organizativa_universidad_id FOREIGN KEY (universidad_id) REFERENCES universidad(id);
ALTER TABLE unidad_organizativa ADD CONSTRAINT fk_unidad_organizativa_localizacion_id FOREIGN KEY (localizacion_id) REFERENCES localizacion(id);
ALTER TABLE unidad_organizativa ADD CONSTRAINT fk_unidad_organizativa_tipo_unidad_id FOREIGN KEY (tipo_unidad_id) REFERENCES tipo_unidad(id);
ALTER TABLE unidad_organizativa ADD CONSTRAINT fk_unidad_organizativa_unidad_padre_id FOREIGN KEY (unidad_padre_id) REFERENCES unidad_organizativa(id);
ALTER TABLE unidad_organizativa ADD CONSTRAINT fk_unidad_organizativa_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE universidades ADD CONSTRAINT fk_universidades_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE categories ADD CONSTRAINT fk_categories_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE cuenta_corriente_alumno ADD CONSTRAINT fk_cuenta_corriente_alumno_universidad_id FOREIGN KEY (universidad_id) REFERENCES universidad(id);
ALTER TABLE cuenta_corriente_alumno ADD CONSTRAINT fk_cuenta_corriente_alumno_estudiante_id FOREIGN KEY (estudiante_id) REFERENCES estudiante(id);
ALTER TABLE cuenta_corriente_alumno ADD CONSTRAINT fk_cuenta_corriente_alumno_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE pago ADD CONSTRAINT fk_pago_universidad_id FOREIGN KEY (universidad_id) REFERENCES universidad(id);
ALTER TABLE pago ADD CONSTRAINT fk_pago_estudiante_id FOREIGN KEY (estudiante_id) REFERENCES estudiante(id);
ALTER TABLE pago ADD CONSTRAINT fk_pago_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE pago_detalle_deuda ADD CONSTRAINT fk_pago_detalle_deuda_pago_id FOREIGN KEY (pago_id) REFERENCES pago(id);
ALTER TABLE pago_detalle_deuda ADD CONSTRAINT fk_pago_detalle_deuda_deuda_id FOREIGN KEY (deuda_id) REFERENCES cuenta_corriente_alumno(id);
ALTER TABLE pago_detalle_deuda ADD CONSTRAINT fk_pago_detalle_deuda_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE auth_usuario ADD CONSTRAINT fk_auth_usuario_rol_id FOREIGN KEY (rol_id) REFERENCES rol(id);
ALTER TABLE auth_usuario ADD CONSTRAINT fk_auth_usuario_universidad_id FOREIGN KEY (universidad_id) REFERENCES universidad(id);
ALTER TABLE auth_usuario ADD CONSTRAINT fk_auth_usuario_persona_id FOREIGN KEY (persona_id) REFERENCES persona(id);
ALTER TABLE auth_usuario ADD CONSTRAINT fk_auth_usuario_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE permiso ADD CONSTRAINT fk_permiso_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE refresh_tokens ADD CONSTRAINT fk_refresh_tokens_auth_usuario_id FOREIGN KEY (auth_usuario_id) REFERENCES auth_usuario(id);
ALTER TABLE refresh_tokens ADD CONSTRAINT fk_refresh_tokens_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE rol ADD CONSTRAINT fk_rol_universidad_id FOREIGN KEY (universidad_id) REFERENCES universidad(id);
ALTER TABLE rol ADD CONSTRAINT fk_rol_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE rol_permiso ADD CONSTRAINT fk_rol_permiso_rol_id FOREIGN KEY (rol_id) REFERENCES rol(id);
ALTER TABLE rol_permiso ADD CONSTRAINT fk_rol_permiso_permiso_id FOREIGN KEY (permiso_id) REFERENCES permiso(id);
ALTER TABLE rol_permiso ADD CONSTRAINT fk_rol_permiso_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
ALTER TABLE users ADD CONSTRAINT fk_users_persona_id FOREIGN KEY (persona_id) REFERENCES persona(id);
ALTER TABLE users ADD CONSTRAINT fk_users_universidad FOREIGN KEY (universidad_id) REFERENCES universidades(id);
