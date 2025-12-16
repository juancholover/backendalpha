-- =====================================================
-- V022__create_simplified_permission_tables.sql
-- Sistema simplificado de permisos con 2 tablas + casbin_rule
-- =====================================================

-- =====================================================
-- 1. TABLA: menu_item (Jerárquica para Islas y Sidebar-Targets)
-- =====================================================
-- id_padre = NULL → ISLA (se lista tras login)
-- id_padre = isla.id → SIDEBAR-TARGET (opción del sidebar)

CREATE TABLE IF NOT EXISTS menu_item (
    id BIGSERIAL PRIMARY KEY,
    id_padre BIGINT REFERENCES menu_item(id) ON DELETE CASCADE,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    ruta_frontend VARCHAR(255),         -- Ruta en el frontend
    icono VARCHAR(50),
    color VARCHAR(20),                  -- Solo para islas
    orden INTEGER DEFAULT 0,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

COMMENT ON TABLE menu_item IS 'Tabla jerárquica para islas (id_padre=NULL) y sidebar-targets (id_padre=isla.id)';
COMMENT ON COLUMN menu_item.id_padre IS 'NULL para islas, ID de isla para sidebar-targets';
COMMENT ON COLUMN menu_item.ruta_frontend IS 'Ruta del frontend para navegación';

CREATE INDEX idx_menu_item_id_padre ON menu_item(id_padre);
CREATE INDEX idx_menu_item_codigo ON menu_item(codigo);
CREATE INDEX idx_menu_item_active ON menu_item(active);

-- =====================================================
-- 2. TABLA: api_permiso (Permisos de API con columnas boolean)
-- =====================================================
CREATE TABLE IF NOT EXISTS api_permiso (
    id BIGSERIAL PRIMARY KEY,
    menu_item_id BIGINT NOT NULL REFERENCES menu_item(id) ON DELETE CASCADE,
    api_base VARCHAR(255) NOT NULL,      -- "/api/v1/universidades"
    descripcion VARCHAR(255) NOT NULL,   -- Descripción para mostrar en UI
    puede_get BOOLEAN DEFAULT false,
    puede_post BOOLEAN DEFAULT false,
    puede_put BOOLEAN DEFAULT false,
    puede_delete BOOLEAN DEFAULT false,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT uk_api_permiso UNIQUE (menu_item_id, api_base)
);

COMMENT ON TABLE api_permiso IS 'Permisos de API con columnas booleanas para cada método HTTP';
COMMENT ON COLUMN api_permiso.api_base IS 'Ruta base de la API, ej: /api/v1/universidades';
COMMENT ON COLUMN api_permiso.descripcion IS 'Descripción para mostrar en la UI del admin';

CREATE INDEX idx_api_permiso_menu_item ON api_permiso(menu_item_id);
CREATE INDEX idx_api_permiso_api_base ON api_permiso(api_base);
CREATE INDEX idx_api_permiso_active ON api_permiso(active);

-- =====================================================
-- 3. TABLA: rol_menu (Asignación de menús a roles)
-- Alternativa a usar casbin_rule con ptype='m'
-- =====================================================
CREATE TABLE IF NOT EXISTS rol_menu (
    id BIGSERIAL PRIMARY KEY,
    rol_nombre VARCHAR(50) NOT NULL,     -- Nombre del rol (ADMIN, PROFESOR, etc.)
    menu_item_id BIGINT NOT NULL REFERENCES menu_item(id) ON DELETE CASCADE,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT uk_rol_menu UNIQUE (rol_nombre, menu_item_id)
);

COMMENT ON TABLE rol_menu IS 'Asignación de menús (islas y sidebar-targets) a roles';

CREATE INDEX idx_rol_menu_rol ON rol_menu(rol_nombre);
CREATE INDEX idx_rol_menu_menu ON rol_menu(menu_item_id);
CREATE INDEX idx_rol_menu_active ON rol_menu(active);

-- =====================================================
-- 4. PERMISOS INDIVIDUALES DE USUARIO (opcional)
-- =====================================================
CREATE TABLE IF NOT EXISTS usuario_menu (
    id BIGSERIAL PRIMARY KEY,
    usuario_email VARCHAR(255) NOT NULL,
    menu_item_id BIGINT NOT NULL REFERENCES menu_item(id) ON DELETE CASCADE,
    otorgado_por VARCHAR(255),
    fecha_otorgamiento TIMESTAMP DEFAULT NOW(),
    fecha_expiracion TIMESTAMP,
    razon TEXT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_usuario_menu UNIQUE (usuario_email, menu_item_id)
);

COMMENT ON TABLE usuario_menu IS 'Permisos individuales de menú para usuarios específicos';

CREATE INDEX idx_usuario_menu_email ON usuario_menu(usuario_email);
CREATE INDEX idx_usuario_menu_item ON usuario_menu(menu_item_id);
CREATE INDEX idx_usuario_menu_expiracion ON usuario_menu(fecha_expiracion);
