-- =====================================================
-- MIGRACIÓN V003: MÓDULOS ACADÉMICOS
-- Tablas de curriculum, matrícula y evaluación
-- =====================================================

-- =====================================================
-- MÓDULO: CURRICULUM
-- =====================================================

-- Tabla: programa_academico
CREATE TABLE programa_academico (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    descripcion TEXT,
    nivel VARCHAR(50) NOT NULL,
    modalidad VARCHAR(50),
    duracion_semestres INTEGER,
    creditos_totales INTEGER,
    unidad_organizativa_id BIGINT NOT NULL,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_programa_unidad FOREIGN KEY (unidad_organizativa_id) REFERENCES unidad_organizativa(id)
);

-- Tabla: plan_academico
CREATE TABLE plan_academico (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    codigo VARCHAR(50) NOT NULL,
    programa_academico_id BIGINT NOT NULL,
    anio_inicio INTEGER NOT NULL,
    anio_fin INTEGER,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_plan_programa FOREIGN KEY (programa_academico_id) REFERENCES programa_academico(id),
    CONSTRAINT uk_plan_codigo_programa UNIQUE (codigo, programa_academico_id)
);

-- Tabla: curso
CREATE TABLE curso (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    descripcion TEXT,
    creditos INTEGER NOT NULL,
    horas_teoria INTEGER,
    horas_practica INTEGER,
    horas_laboratorio INTEGER,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Tabla: plan_curso
CREATE TABLE plan_curso (
    id BIGSERIAL PRIMARY KEY,
    plan_academico_id BIGINT NOT NULL,
    curso_id BIGINT NOT NULL,
    semestre INTEGER NOT NULL,
    tipo VARCHAR(50),
    es_obligatorio BOOLEAN DEFAULT true,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_plan_curso_plan FOREIGN KEY (plan_academico_id) REFERENCES plan_academico(id),
    CONSTRAINT fk_plan_curso_curso FOREIGN KEY (curso_id) REFERENCES curso(id),
    CONSTRAINT uk_plan_curso UNIQUE (plan_academico_id, curso_id)
);

-- Tabla: requisito_curso (pre-requisitos)
CREATE TABLE requisito_curso (
    id BIGSERIAL PRIMARY KEY,
    curso_id BIGINT NOT NULL,
    curso_requisito_id BIGINT NOT NULL,
    tipo VARCHAR(20) DEFAULT 'PRE_REQUISITO',
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_requisito_curso FOREIGN KEY (curso_id) REFERENCES curso(id),
    CONSTRAINT fk_curso_requisito FOREIGN KEY (curso_requisito_id) REFERENCES curso(id),
    CONSTRAINT uk_requisito_curso UNIQUE (curso_id, curso_requisito_id)
);

-- Tabla: silabo
CREATE TABLE silabo (
    id BIGSERIAL PRIMARY KEY,
    curso_id BIGINT NOT NULL,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    competencias TEXT,
    metodologia TEXT,
    evaluacion TEXT,
    bibliografia TEXT,
    version INTEGER DEFAULT 1,
    estado VARCHAR(20) DEFAULT 'BORRADOR',
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_silabo_curso FOREIGN KEY (curso_id) REFERENCES curso(id)
);

-- Tabla: silabo_unidad
CREATE TABLE silabo_unidad (
    id BIGSERIAL PRIMARY KEY,
    silabo_id BIGINT NOT NULL,
    numero INTEGER NOT NULL,
    nombre VARCHAR(200) NOT NULL,
    objetivos TEXT,
    contenidos TEXT,
    duracion_semanas INTEGER,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_silabo_unidad_silabo FOREIGN KEY (silabo_id) REFERENCES silabo(id) ON DELETE CASCADE,
    CONSTRAINT uk_silabo_unidad UNIQUE (silabo_id, numero)
);

-- Tabla: silabo_actividad
CREATE TABLE silabo_actividad (
    id BIGSERIAL PRIMARY KEY,
    silabo_unidad_id BIGINT NOT NULL,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    tipo VARCHAR(50),
    semana INTEGER,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_actividad_unidad FOREIGN KEY (silabo_unidad_id) REFERENCES silabo_unidad(id) ON DELETE CASCADE
);

-- Tabla: silabo_calidad
CREATE TABLE silabo_calidad (
    id BIGSERIAL PRIMARY KEY,
    silabo_id BIGINT NOT NULL,
    puntaje_total NUMERIC(5,2) NOT NULL,
    puntaje_competencias NUMERIC(5,2),
    puntaje_metodologia NUMERIC(5,2),
    puntaje_evaluacion NUMERIC(5,2),
    puntaje_bibliografia NUMERIC(5,2),
    aprobado BOOLEAN DEFAULT false,
    observaciones TEXT,
    fecha_evaluacion TIMESTAMP NOT NULL DEFAULT NOW(),
    evaluador VARCHAR(100),
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_calidad_silabo FOREIGN KEY (silabo_id) REFERENCES silabo(id) ON DELETE CASCADE
);

-- Tabla: silabo_historial
CREATE TABLE silabo_historial (
    id BIGSERIAL PRIMARY KEY,
    silabo_id BIGINT NOT NULL,
    version INTEGER NOT NULL,
    campo_modificado VARCHAR(100) NOT NULL,
    valor_anterior TEXT,
    valor_nuevo TEXT,
    motivo_cambio TEXT,
    fecha_modificacion TIMESTAMP NOT NULL DEFAULT NOW(),
    usuario_modificacion VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_historial_silabo FOREIGN KEY (silabo_id) REFERENCES silabo(id) ON DELETE CASCADE
);

-- Tabla: silabo_plantilla
CREATE TABLE silabo_plantilla (
    id BIGSERIAL PRIMARY KEY,
    silabo_id BIGINT NOT NULL UNIQUE,
    nombre_plantilla VARCHAR(200) NOT NULL,
    descripcion TEXT,
    es_oficial BOOLEAN DEFAULT false,
    fecha_publicacion TIMESTAMP,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_plantilla_silabo FOREIGN KEY (silabo_id) REFERENCES silabo(id)
);

-- =====================================================
-- MÓDULO: MATRÍCULA
-- =====================================================

-- Tabla: periodo_academico
CREATE TABLE periodo_academico (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Tabla: estudiante
CREATE TABLE estudiante (
    id BIGSERIAL PRIMARY KEY,
    persona_id BIGINT NOT NULL UNIQUE,
    codigo_estudiante VARCHAR(20) NOT NULL UNIQUE,
    programa_id BIGINT NOT NULL,
    fecha_ingreso DATE NOT NULL,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_estudiante_persona FOREIGN KEY (persona_id) REFERENCES persona(id),
    CONSTRAINT fk_estudiante_programa FOREIGN KEY (programa_id) REFERENCES programa_academico(id)
);

-- Tabla: modalidad
CREATE TABLE modalidad (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Tabla: curso_ofertado
CREATE TABLE curso_ofertado (
    id BIGSERIAL PRIMARY KEY,
    plan_curso_id BIGINT NOT NULL,
    periodo_academico_id BIGINT NOT NULL,
    profesor_id BIGINT,
    modalidad_id BIGINT,
    silabo_id BIGINT,
    localizacion_id BIGINT,
    cupos_disponibles INTEGER,
    estado VARCHAR(20) DEFAULT 'PLANIFICADO',
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_curso_ofertado_plan_curso FOREIGN KEY (plan_curso_id) REFERENCES plan_curso(id),
    CONSTRAINT fk_curso_ofertado_periodo FOREIGN KEY (periodo_academico_id) REFERENCES periodo_academico(id),
    CONSTRAINT fk_curso_ofertado_profesor FOREIGN KEY (profesor_id) REFERENCES profesor(id),
    CONSTRAINT fk_curso_ofertado_modalidad FOREIGN KEY (modalidad_id) REFERENCES modalidad(id),
    CONSTRAINT fk_curso_ofertado_silabo FOREIGN KEY (silabo_id) REFERENCES silabo(id),
    CONSTRAINT fk_curso_ofertado_localizacion FOREIGN KEY (localizacion_id) REFERENCES localizacion(id)
);

-- Tabla: horario
CREATE TABLE horario (
    id BIGSERIAL PRIMARY KEY,
    curso_ofertado_id BIGINT NOT NULL,
    dia_semana INTEGER NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    localizacion_id BIGINT,
    tipo VARCHAR(20) DEFAULT 'TEORIA',
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_horario_curso_ofertado FOREIGN KEY (curso_ofertado_id) REFERENCES curso_ofertado(id),
    CONSTRAINT fk_horario_localizacion FOREIGN KEY (localizacion_id) REFERENCES localizacion(id)
);

-- Tabla: matricula
CREATE TABLE matricula (
    id BIGSERIAL PRIMARY KEY,
    estudiante_id BIGINT NOT NULL,
    curso_ofertado_id BIGINT NOT NULL,
    fecha_matricula TIMESTAMP NOT NULL DEFAULT NOW(),
    estado VARCHAR(20) DEFAULT 'MATRICULADO',
    nota_final NUMERIC(4,2),
    observaciones TEXT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_matricula_estudiante FOREIGN KEY (estudiante_id) REFERENCES estudiante(id),
    CONSTRAINT fk_matricula_curso_ofertado FOREIGN KEY (curso_ofertado_id) REFERENCES curso_ofertado(id),
    CONSTRAINT uk_matricula UNIQUE (estudiante_id, curso_ofertado_id)
);

-- =====================================================
-- MÓDULO: EVALUACIÓN
-- =====================================================

-- Tabla: evaluacion_criterio
CREATE TABLE evaluacion_criterio (
    id BIGSERIAL PRIMARY KEY,
    curso_ofertado_id BIGINT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    peso NUMERIC(5,2) NOT NULL,
    tipo VARCHAR(50),
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_criterio_curso_ofertado FOREIGN KEY (curso_ofertado_id) REFERENCES curso_ofertado(id)
);

-- Tabla: evaluacion_nota
CREATE TABLE evaluacion_nota (
    id BIGSERIAL PRIMARY KEY,
    matricula_id BIGINT NOT NULL,
    criterio_id BIGINT NOT NULL,
    nota NUMERIC(4,2) NOT NULL,
    fecha_registro TIMESTAMP NOT NULL DEFAULT NOW(),
    observaciones TEXT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_nota_matricula FOREIGN KEY (matricula_id) REFERENCES matricula(id),
    CONSTRAINT fk_nota_criterio FOREIGN KEY (criterio_id) REFERENCES evaluacion_criterio(id),
    CONSTRAINT uk_evaluacion_nota UNIQUE (matricula_id, criterio_id)
);

-- =====================================================
-- MÓDULO: FINANZAS
-- =====================================================

-- Tabla: cuenta_corriente_alumno
CREATE TABLE cuenta_corriente_alumno (
    id BIGSERIAL PRIMARY KEY,
    estudiante_id BIGINT NOT NULL,
    concepto VARCHAR(200) NOT NULL,
    monto NUMERIC(10,2) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    estado VARCHAR(20) DEFAULT 'PENDIENTE',
    fecha_vencimiento DATE,
    periodo_academico_id BIGINT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_cuenta_estudiante FOREIGN KEY (estudiante_id) REFERENCES estudiante(id),
    CONSTRAINT fk_cuenta_periodo FOREIGN KEY (periodo_academico_id) REFERENCES periodo_academico(id)
);

-- Tabla: pago
CREATE TABLE pago (
    id BIGSERIAL PRIMARY KEY,
    estudiante_id BIGINT NOT NULL,
    monto_total NUMERIC(10,2) NOT NULL,
    fecha_pago TIMESTAMP NOT NULL DEFAULT NOW(),
    medio_pago VARCHAR(50) NOT NULL,
    numero_transaccion VARCHAR(100),
    estado VARCHAR(20) DEFAULT 'COMPLETADO',
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_pago_estudiante FOREIGN KEY (estudiante_id) REFERENCES estudiante(id)
);

-- Tabla: pago_detalle_deuda
CREATE TABLE pago_detalle_deuda (
    id BIGSERIAL PRIMARY KEY,
    pago_id BIGINT NOT NULL,
    deuda_id BIGINT NOT NULL,
    monto_aplicado NUMERIC(10,2) NOT NULL,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_detalle_pago FOREIGN KEY (pago_id) REFERENCES pago(id),
    CONSTRAINT fk_detalle_deuda FOREIGN KEY (deuda_id) REFERENCES cuenta_corriente_alumno(id)
);

-- =====================================================
-- ÍNDICES PARA OPTIMIZACIÓN
-- =====================================================

CREATE INDEX idx_programa_codigo ON programa_academico(codigo);
CREATE INDEX idx_curso_codigo ON curso(codigo);
CREATE INDEX idx_estudiante_codigo ON estudiante(codigo_estudiante);
CREATE INDEX idx_estudiante_programa ON estudiante(programa_id);
CREATE INDEX idx_matricula_estudiante ON matricula(estudiante_id);
CREATE INDEX idx_matricula_curso ON matricula(curso_ofertado_id);
CREATE INDEX idx_curso_ofertado_periodo ON curso_ofertado(periodo_academico_id);
CREATE INDEX idx_silabo_curso ON silabo(curso_id);
CREATE INDEX idx_evaluacion_matricula ON evaluacion_nota(matricula_id);
CREATE INDEX idx_cuenta_estudiante ON cuenta_corriente_alumno(estudiante_id);
CREATE INDEX idx_pago_estudiante ON pago(estudiante_id);

-- =====================================================
-- COMENTARIOS DE TABLAS
-- =====================================================

COMMENT ON TABLE programa_academico IS 'Programas académicos ofrecidos por la institución';
COMMENT ON TABLE curso IS 'Catálogo de cursos disponibles';
COMMENT ON TABLE silabo IS 'Sílabos de cursos con contenido académico';
COMMENT ON TABLE estudiante IS 'Estudiantes matriculados en programas académicos';
COMMENT ON TABLE curso_ofertado IS 'Cursos ofertados en periodos académicos específicos';
COMMENT ON TABLE matricula IS 'Matrículas de estudiantes en cursos ofertados';
COMMENT ON TABLE evaluacion_nota IS 'Notas de estudiantes por criterio de evaluación';
COMMENT ON TABLE cuenta_corriente_alumno IS 'Deudas y cargos de estudiantes';
COMMENT ON TABLE pago IS 'Pagos realizados por estudiantes';
