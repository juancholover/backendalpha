-- =====================================================
-- TABLAS PARA PLANTILLAS MULTI-CAMPUS
-- =====================================================

-- Tabla: silabo_plantilla
-- Almacena plantillas de sílabos aprobados para publicación masiva
CREATE TABLE silabo_plantilla (
    id BIGSERIAL PRIMARY KEY,
    silabo_id BIGINT NOT NULL UNIQUE,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVA', -- ACTIVA, OBSOLETA, SUSPENDIDA
    fecha_inicio_vigencia DATE,
    fecha_fin_vigencia DATE,
    autorizado_por VARCHAR(200),
    fecha_autorizacion DATE,
    notas TEXT,
    nivel_flexibilidad VARCHAR(30) NOT NULL DEFAULT 'FECHAS_Y_PONDERACION', -- SOLO_FECHAS, FECHAS_Y_PONDERACION, SIN_MODIFICACION
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    
    CONSTRAINT fk_plantilla_silabo FOREIGN KEY (silabo_id) REFERENCES silabo(id),
    CONSTRAINT chk_plantilla_estado CHECK (estado IN ('ACTIVA', 'OBSOLETA', 'SUSPENDIDA')),
    CONSTRAINT chk_plantilla_flexibilidad CHECK (nivel_flexibilidad IN ('SOLO_FECHAS', 'FECHAS_Y_PONDERACION', 'SIN_MODIFICACION'))
);

CREATE INDEX idx_plantilla_silabo ON silabo_plantilla(silabo_id);
CREATE INDEX idx_plantilla_estado ON silabo_plantilla(estado);
CREATE INDEX idx_plantilla_vigencia ON silabo_plantilla(fecha_inicio_vigencia, fecha_fin_vigencia);

COMMENT ON TABLE silabo_plantilla IS 'Plantillas de sílabos aprobados para publicación en múltiples campus';
COMMENT ON COLUMN silabo_plantilla.nivel_flexibilidad IS 'Define qué pueden modificar los campus: SOLO_FECHAS, FECHAS_Y_PONDERACION, SIN_MODIFICACION';

-- Tabla: silabo_publicacion
-- Almacena publicaciones de plantillas en campus específicos
CREATE TABLE silabo_publicacion (
    id BIGSERIAL PRIMARY KEY,
    plantilla_id BIGINT NOT NULL,
    localizacion_id BIGINT NOT NULL,
    silabo_campus_id BIGINT, -- Sílabo COPIA creado para el campus
    anio_academico VARCHAR(10) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE', -- PENDIENTE, ACTIVA, FINALIZADA, CANCELADA
    fecha_publicacion DATE,
    publicado_por VARCHAR(200),
    fecha_inicio_campus DATE,
    fecha_fin_campus DATE,
    adaptado BOOLEAN NOT NULL DEFAULT FALSE,
    adaptado_por VARCHAR(200),
    fecha_adaptacion DATE,
    notas_adaptacion TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    
    CONSTRAINT fk_publicacion_plantilla FOREIGN KEY (plantilla_id) REFERENCES silabo_plantilla(id),
    CONSTRAINT fk_publicacion_localizacion FOREIGN KEY (localizacion_id) REFERENCES localizacion(id),
    CONSTRAINT fk_publicacion_silabo_campus FOREIGN KEY (silabo_campus_id) REFERENCES silabo(id),
    CONSTRAINT chk_publicacion_estado CHECK (estado IN ('PENDIENTE', 'ACTIVA', 'FINALIZADA', 'CANCELADA')),
    CONSTRAINT uk_publicacion_plantilla_campus_anio UNIQUE (plantilla_id, localizacion_id, anio_academico)
);

CREATE INDEX idx_publicacion_plantilla ON silabo_publicacion(plantilla_id);
CREATE INDEX idx_publicacion_localizacion ON silabo_publicacion(localizacion_id);
CREATE INDEX idx_publicacion_estado ON silabo_publicacion(estado);
CREATE INDEX idx_publicacion_anio ON silabo_publicacion(anio_academico);
CREATE INDEX idx_publicacion_adaptado ON silabo_publicacion(adaptado);

COMMENT ON TABLE silabo_publicacion IS 'Publicaciones de plantillas de sílabos en campus específicos';
COMMENT ON COLUMN silabo_publicacion.silabo_campus_id IS 'Sílabo copia creado para el campus (puede ser modificado según restricciones)';
COMMENT ON COLUMN silabo_publicacion.adaptado IS 'Indica si el campus ya realizó adaptaciones permitidas';

-- =====================================================
-- DATOS DE EJEMPLO (OPCIONAL - PARA TESTING)
-- =====================================================

-- Nota: Estos INSERT son solo ejemplos. 
-- En producción, las plantillas se crean mediante la API.

-- Ejemplo: Crear plantilla para un sílabo existente
-- INSERT INTO silabo_plantilla (silabo_id, autorizado_por, fecha_autorizacion, nivel_flexibilidad, notas)
-- VALUES (1, 'director@upeu.edu.pe', CURRENT_DATE, 'FECHAS_Y_PONDERACION', 'Plantilla aprobada para todos los campus 2025');

-- Ejemplo: Publicar en campus
-- INSERT INTO silabo_publicacion (plantilla_id, localizacion_id, anio_academico, estado, publicado_por, fecha_publicacion, fecha_inicio_campus, fecha_fin_campus)
-- VALUES (1, 1, '2025', 'ACTIVA', 'admin@upeu.edu.pe', CURRENT_DATE, '2025-03-01', '2025-07-31');
