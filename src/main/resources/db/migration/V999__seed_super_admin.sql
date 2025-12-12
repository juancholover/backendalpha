-- =====================================================
-- SCRIPT DE INICIALIZACIÓN: SUPER ADMIN
-- Crear usuario super administrador con acceso completo
-- Fecha: 2025-12-12
-- =====================================================

-- =====================================================
-- 1. CREAR ROL SUPER_ADMIN
-- =====================================================
INSERT INTO rol (nombre, descripcion, es_sistema, active, created_at, updated_at, created_by, updated_by)
VALUES 
    ('SUPER_ADMIN', 'Administrador del Sistema con acceso total', true, true, NOW(), NOW(), 'system', 'system')
ON CONFLICT (nombre) DO NOTHING;

-- =====================================================
-- 2. CREAR PERMISOS PARA TODOS LOS MÓDULOS
-- =====================================================

-- Módulo: SEGURIDAD
INSERT INTO permiso (nombre_clave, descripcion, modulo, recurso, accion, active, created_at, updated_at, created_by, updated_by)
VALUES
    -- Usuarios
    ('USUARIOS_CREAR', 'Crear nuevos usuarios', 'SEGURIDAD', 'USUARIOS', 'CREAR', true, NOW(), NOW(), 'system', 'system'),
    ('USUARIOS_LEER', 'Ver usuarios del sistema', 'SEGURIDAD', 'USUARIOS', 'LEER', true, NOW(), NOW(), 'system', 'system'),
    ('USUARIOS_ACTUALIZAR', 'Editar usuarios', 'SEGURIDAD', 'USUARIOS', 'ACTUALIZAR', true, NOW(), NOW(), 'system', 'system'),
    ('USUARIOS_ELIMINAR', 'Eliminar usuarios', 'SEGURIDAD', 'USUARIOS', 'ELIMINAR', true, NOW(), NOW(), 'system', 'system'),
    
    -- Roles
    ('ROLES_CREAR', 'Crear roles', 'SEGURIDAD', 'ROLES', 'CREAR', true, NOW(), NOW(), 'system', 'system'),
    ('ROLES_LEER', 'Ver roles', 'SEGURIDAD', 'ROLES', 'LEER', true, NOW(), NOW(), 'system', 'system'),
    ('ROLES_ACTUALIZAR', 'Editar roles', 'SEGURIDAD', 'ROLES', 'ACTUALIZAR', true, NOW(), NOW(), 'system', 'system'),
    ('ROLES_ELIMINAR', 'Eliminar roles', 'SEGURIDAD', 'ROLES', 'ELIMINAR', true, NOW(), NOW(), 'system', 'system'),
    
    -- Permisos
    ('PERMISOS_CREAR', 'Crear permisos', 'SEGURIDAD', 'PERMISOS', 'CREAR', true, NOW(), NOW(), 'system', 'system'),
    ('PERMISOS_LEER', 'Ver permisos', 'SEGURIDAD', 'PERMISOS', 'LEER', true, NOW(), NOW(), 'system', 'system'),
    ('PERMISOS_ACTUALIZAR', 'Editar permisos', 'SEGURIDAD', 'PERMISOS', 'ACTUALIZAR', true, NOW(), NOW(), 'system', 'system'),
    ('PERMISOS_ELIMINAR', 'Eliminar permisos', 'SEGURIDAD', 'PERMISOS', 'ELIMINAR', true, NOW(), NOW(), 'system', 'system')
ON CONFLICT (nombre_clave) DO NOTHING;

-- Módulo: CURRICULUM
INSERT INTO permiso (nombre_clave, descripcion, modulo, recurso, accion, active, created_at, updated_at, created_by, updated_by)
VALUES
    -- Sílabos
    ('SILABOS_CREAR', 'Crear sílabos', 'CURRICULUM', 'SILABOS', 'CREAR', true, NOW(), NOW(), 'system', 'system'),
    ('SILABOS_LEER', 'Ver sílabos', 'CURRICULUM', 'SILABOS', 'LEER', true, NOW(), NOW(), 'system', 'system'),
    ('SILABOS_ACTUALIZAR', 'Editar sílabos', 'CURRICULUM', 'SILABOS', 'ACTUALIZAR', true, NOW(), NOW(), 'system', 'system'),
    ('SILABOS_ELIMINAR', 'Eliminar sílabos', 'CURRICULUM', 'SILABOS', 'ELIMINAR', true, NOW(), NOW(), 'system', 'system'),
    ('SILABOS_APROBAR', 'Aprobar sílabos', 'CURRICULUM', 'SILABOS', 'APROBAR', true, NOW(), NOW(), 'system', 'system'),
    ('SILABOS_PUBLICAR', 'Publicar plantillas de sílabos', 'CURRICULUM', 'SILABOS', 'PUBLICAR', true, NOW(), NOW(), 'system', 'system'),
    
    -- Cursos
    ('CURSOS_CREAR', 'Crear cursos', 'CURRICULUM', 'CURSOS', 'CREAR', true, NOW(), NOW(), 'system', 'system'),
    ('CURSOS_LEER', 'Ver cursos', 'CURRICULUM', 'CURSOS', 'LEER', true, NOW(), NOW(), 'system', 'system'),
    ('CURSOS_ACTUALIZAR', 'Editar cursos', 'CURRICULUM', 'CURSOS', 'ACTUALIZAR', true, NOW(), NOW(), 'system', 'system'),
    ('CURSOS_ELIMINAR', 'Eliminar cursos', 'CURRICULUM', 'CURSOS', 'ELIMINAR', true, NOW(), NOW(), 'system', 'system'),
    
    -- Programas Académicos
    ('PROGRAMAS_CREAR', 'Crear programas académicos', 'CURRICULUM', 'PROGRAMAS', 'CREAR', true, NOW(), NOW(), 'system', 'system'),
    ('PROGRAMAS_LEER', 'Ver programas académicos', 'CURRICULUM', 'PROGRAMAS', 'LEER', true, NOW(), NOW(), 'system', 'system'),
    ('PROGRAMAS_ACTUALIZAR', 'Editar programas académicos', 'CURRICULUM', 'PROGRAMAS', 'ACTUALIZAR', true, NOW(), NOW(), 'system', 'system'),
    ('PROGRAMAS_ELIMINAR', 'Eliminar programas académicos', 'CURRICULUM', 'PROGRAMAS', 'ELIMINAR', true, NOW(), NOW(), 'system', 'system')
ON CONFLICT (nombre_clave) DO NOTHING;

-- Módulo: MATRICULA
INSERT INTO permiso (nombre_clave, descripcion, modulo, recurso, accion, active, created_at, updated_at, created_by, updated_by)
VALUES
    ('MATRICULAS_CREAR', 'Matricular estudiantes', 'MATRICULA', 'MATRICULAS', 'CREAR', true, NOW(), NOW(), 'system', 'system'),
    ('MATRICULAS_LEER', 'Ver matrículas', 'MATRICULA', 'MATRICULAS', 'LEER', true, NOW(), NOW(), 'system', 'system'),
    ('MATRICULAS_ACTUALIZAR', 'Editar matrículas', 'MATRICULA', 'MATRICULAS', 'ACTUALIZAR', true, NOW(), NOW(), 'system', 'system'),
    ('MATRICULAS_ELIMINAR', 'Anular matrículas', 'MATRICULA', 'MATRICULAS', 'ELIMINAR', true, NOW(), NOW(), 'system', 'system'),
    
    -- Horarios
    ('HORARIOS_CREAR', 'Crear horarios', 'MATRICULA', 'HORARIOS', 'CREAR', true, NOW(), NOW(), 'system', 'system'),
    ('HORARIOS_LEER', 'Ver horarios', 'MATRICULA', 'HORARIOS', 'LEER', true, NOW(), NOW(), 'system', 'system'),
    ('HORARIOS_ACTUALIZAR', 'Editar horarios', 'MATRICULA', 'HORARIOS', 'ACTUALIZAR', true, NOW(), NOW(), 'system', 'system'),
    ('HORARIOS_ELIMINAR', 'Eliminar horarios', 'MATRICULA', 'HORARIOS', 'ELIMINAR', true, NOW(), NOW(), 'system', 'system')
ON CONFLICT (nombre_clave) DO NOTHING;

-- Módulo: FINANZAS
INSERT INTO permiso (nombre_clave, descripcion, modulo, recurso, accion, active, created_at, updated_at, created_by, updated_by)
VALUES
    ('PAGOS_CREAR', 'Registrar pagos', 'FINANZAS', 'PAGOS', 'CREAR', true, NOW(), NOW(), 'system', 'system'),
    ('PAGOS_LEER', 'Ver pagos', 'FINANZAS', 'PAGOS', 'LEER', true, NOW(), NOW(), 'system', 'system'),
    ('PAGOS_ACTUALIZAR', 'Modificar pagos', 'FINANZAS', 'PAGOS', 'ACTUALIZAR', true, NOW(), NOW(), 'system', 'system'),
    ('PAGOS_ANULAR', 'Anular pagos', 'FINANZAS', 'PAGOS', 'ANULAR', true, NOW(), NOW(), 'system', 'system'),
    
    -- Cuentas Corrientes
    ('CUENTAS_LEER', 'Ver cuentas corrientes', 'FINANZAS', 'CUENTAS', 'LEER', true, NOW(), NOW(), 'system', 'system'),
    ('CUENTAS_ACTUALIZAR', 'Ajustar cuentas corrientes', 'FINANZAS', 'CUENTAS', 'ACTUALIZAR', true, NOW(), NOW(), 'system', 'system')
ON CONFLICT (nombre_clave) DO NOTHING;

-- Módulo: EVALUACION
INSERT INTO permiso (nombre_clave, descripcion, modulo, recurso, accion, active, created_at, updated_at, created_by, updated_by)
VALUES
    ('NOTAS_CREAR', 'Registrar notas', 'EVALUACION', 'NOTAS', 'CREAR', true, NOW(), NOW(), 'system', 'system'),
    ('NOTAS_LEER', 'Ver notas', 'EVALUACION', 'NOTAS', 'LEER', true, NOW(), NOW(), 'system', 'system'),
    ('NOTAS_ACTUALIZAR', 'Editar notas', 'EVALUACION', 'NOTAS', 'ACTUALIZAR', true, NOW(), NOW(), 'system', 'system'),
    ('NOTAS_APROBAR', 'Aprobar cierre de notas', 'EVALUACION', 'NOTAS', 'APROBAR', true, NOW(), NOW(), 'system', 'system'),
    
    -- Asistencias
    ('ASISTENCIAS_CREAR', 'Registrar asistencias', 'EVALUACION', 'ASISTENCIAS', 'CREAR', true, NOW(), NOW(), 'system', 'system'),
    ('ASISTENCIAS_LEER', 'Ver asistencias', 'EVALUACION', 'ASISTENCIAS', 'LEER', true, NOW(), NOW(), 'system', 'system'),
    ('ASISTENCIAS_ACTUALIZAR', 'Modificar asistencias', 'EVALUACION', 'ASISTENCIAS', 'ACTUALIZAR', true, NOW(), NOW(), 'system', 'system')
ON CONFLICT (nombre_clave) DO NOTHING;

-- Módulo: SISTEMA
INSERT INTO permiso (nombre_clave, descripcion, modulo, recurso, accion, active, created_at, updated_at, created_by, updated_by)
VALUES
    ('LOGS_LEER', 'Ver logs del sistema', 'SISTEMA', 'LOGS', 'LEER', true, NOW(), NOW(), 'system', 'system'),
    ('BACKUPS_CREAR', 'Ejecutar backups', 'SISTEMA', 'BACKUPS', 'CREAR', true, NOW(), NOW(), 'system', 'system'),
    ('BACKUPS_LEER', 'Ver historial de backups', 'SISTEMA', 'BACKUPS', 'LEER', true, NOW(), NOW(), 'system', 'system'),
    ('AUDITORIA_LEER', 'Ver auditorías', 'SISTEMA', 'AUDITORIA', 'LEER', true, NOW(), NOW(), 'system', 'system'),
    ('CONFIGURACION_ACTUALIZAR', 'Modificar configuración del sistema', 'SISTEMA', 'CONFIGURACION', 'ACTUALIZAR', true, NOW(), NOW(), 'system', 'system')
ON CONFLICT (nombre_clave) DO NOTHING;

-- Módulo: PEOPLE (Personas, Empleados, Autoridades)
INSERT INTO permiso (nombre_clave, descripcion, modulo, recurso, accion, active, created_at, updated_at, created_by, updated_by)
VALUES
    ('PERSONAS_CREAR', 'Crear personas', 'PEOPLE', 'PERSONAS', 'CREAR', true, NOW(), NOW(), 'system', 'system'),
    ('PERSONAS_LEER', 'Ver personas', 'PEOPLE', 'PERSONAS', 'LEER', true, NOW(), NOW(), 'system', 'system'),
    ('PERSONAS_ACTUALIZAR', 'Editar personas', 'PEOPLE', 'PERSONAS', 'ACTUALIZAR', true, NOW(), NOW(), 'system', 'system'),
    ('PERSONAS_ELIMINAR', 'Eliminar personas', 'PEOPLE', 'PERSONAS', 'ELIMINAR', true, NOW(), NOW(), 'system', 'system'),
    
    -- Empleados
    ('EMPLEADOS_CREAR', 'Crear empleados', 'PEOPLE', 'EMPLEADOS', 'CREAR', true, NOW(), NOW(), 'system', 'system'),
    ('EMPLEADOS_LEER', 'Ver empleados', 'PEOPLE', 'EMPLEADOS', 'LEER', true, NOW(), NOW(), 'system', 'system'),
    ('EMPLEADOS_ACTUALIZAR', 'Editar empleados', 'PEOPLE', 'EMPLEADOS', 'ACTUALIZAR', true, NOW(), NOW(), 'system', 'system'),
    
    -- Autoridades
    ('AUTORIDADES_CREAR', 'Designar autoridades', 'PEOPLE', 'AUTORIDADES', 'CREAR', true, NOW(), NOW(), 'system', 'system'),
    ('AUTORIDADES_LEER', 'Ver autoridades', 'PEOPLE', 'AUTORIDADES', 'LEER', true, NOW(), NOW(), 'system', 'system'),
    ('AUTORIDADES_ACTUALIZAR', 'Modificar autoridades', 'PEOPLE', 'AUTORIDADES', 'ACTUALIZAR', true, NOW(), NOW(), 'system', 'system')
ON CONFLICT (nombre_clave) DO NOTHING;

-- Módulo: CORE (Universidad, Unidades Organizativas, Localizaciones)
INSERT INTO permiso (nombre_clave, descripcion, modulo, recurso, accion, active, created_at, updated_at, created_by, updated_by)
VALUES
    ('UNIVERSIDADES_CREAR', 'Crear universidades', 'CORE', 'UNIVERSIDADES', 'CREAR', true, NOW(), NOW(), 'system', 'system'),
    ('UNIVERSIDADES_LEER', 'Ver universidades', 'CORE', 'UNIVERSIDADES', 'LEER', true, NOW(), NOW(), 'system', 'system'),
    ('UNIVERSIDADES_ACTUALIZAR', 'Editar universidades', 'CORE', 'UNIVERSIDADES', 'ACTUALIZAR', true, NOW(), NOW(), 'system', 'system'),
    
    -- Unidades Organizativas
    ('UNIDADES_CREAR', 'Crear unidades organizativas', 'CORE', 'UNIDADES', 'CREAR', true, NOW(), NOW(), 'system', 'system'),
    ('UNIDADES_LEER', 'Ver unidades organizativas', 'CORE', 'UNIDADES', 'LEER', true, NOW(), NOW(), 'system', 'system'),
    ('UNIDADES_ACTUALIZAR', 'Editar unidades organizativas', 'CORE', 'UNIDADES', 'ACTUALIZAR', true, NOW(), NOW(), 'system', 'system'),
    
    -- Localizaciones
    ('LOCALIZACIONES_CREAR', 'Crear localizaciones', 'CORE', 'LOCALIZACIONES', 'CREAR', true, NOW(), NOW(), 'system', 'system'),
    ('LOCALIZACIONES_LEER', 'Ver localizaciones', 'CORE', 'LOCALIZACIONES', 'LEER', true, NOW(), NOW(), 'system', 'system'),
    ('LOCALIZACIONES_ACTUALIZAR', 'Editar localizaciones', 'CORE', 'LOCALIZACIONES', 'ACTUALIZAR', true, NOW(), NOW(), 'system', 'system')
ON CONFLICT (nombre_clave) DO NOTHING;

-- =====================================================
-- 3. ASIGNAR TODOS LOS PERMISOS AL ROL SUPER_ADMIN
-- =====================================================
INSERT INTO rol_permiso (rol_id, permiso_id, active, created_at, updated_at, created_by, updated_by)
SELECT 
    (SELECT id FROM rol WHERE nombre = 'SUPER_ADMIN'),
    p.id,
    true,
    NOW(),
    NOW(),
    'system',
    'system'
FROM permiso p
WHERE NOT EXISTS (
    SELECT 1 
    FROM rol_permiso rp 
    WHERE rp.rol_id = (SELECT id FROM rol WHERE nombre = 'SUPER_ADMIN')
    AND rp.permiso_id = p.id
);

-- =====================================================
-- 4. CREAR PERSONA PARA SUPER ADMIN
-- =====================================================
INSERT INTO persona (
    nombres, apellido_paterno, apellido_materno, 
    tipo_documento, numero_documento, 
    email, telefono, fecha_nacimiento, genero,
    active, created_at, updated_at, created_by, updated_by
)
VALUES (
    'Super', 'Admin', 'Sistema',
    'DNI', '00000000',
    'superadmin@upeu.edu.pe', '999999999', '1990-01-01', 'MASCULINO',
    true, NOW(), NOW(), 'system', 'system'
)
ON CONFLICT (numero_documento) DO NOTHING;

-- =====================================================
-- 5. CREAR USUARIO SUPER_ADMIN
-- =====================================================
-- Password por defecto: "SuperAdmin2025!" 
-- Hash BCrypt generado con cost factor 10
INSERT INTO auth_usuario (
    persona_id, rol_id, password_hash, 
    ultimo_acceso, intentos_fallidos, 
    requiere_cambio_password, fecha_ultimo_cambio_password,
    active, created_at, updated_at, created_by, updated_by
)
VALUES (
    (SELECT id FROM persona WHERE numero_documento = '00000000'),
    (SELECT id FROM rol WHERE nombre = 'SUPER_ADMIN'),
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- SuperAdmin2025!
    NULL, 0,
    true, NOW(),
    true, NOW(), NOW(), 'system', 'system'
)
ON CONFLICT (persona_id) DO NOTHING;

-- =====================================================
-- 6. INFORMACIÓN DE ACCESO
-- =====================================================
-- Usuario creado:
--   Email/Username: superadmin@upeu.edu.pe
--   Password temporal: SuperAdmin2025!
--   Rol: SUPER_ADMIN
--
-- IMPORTANTE: 
-- 1. Cambiar el password en el primer acceso
-- 2. Este usuario tiene acceso total al sistema
-- 3. Usar solo para tareas administrativas
-- =====================================================

-- Verificación de creación exitosa
DO $$
DECLARE
    v_rol_id BIGINT;
    v_usuario_id BIGINT;
    v_permisos_count INT;
BEGIN
    SELECT id INTO v_rol_id FROM rol WHERE nombre = 'SUPER_ADMIN';
    SELECT id INTO v_usuario_id FROM auth_usuario WHERE persona_id = (SELECT id FROM persona WHERE numero_documento = '00000000');
    SELECT COUNT(*) INTO v_permisos_count FROM rol_permiso WHERE rol_id = v_rol_id;
    
    RAISE NOTICE '========================================';
    RAISE NOTICE 'SUPER ADMIN CREADO EXITOSAMENTE';
    RAISE NOTICE '========================================';
    RAISE NOTICE 'Rol ID: %', v_rol_id;
    RAISE NOTICE 'Usuario ID: %', v_usuario_id;
    RAISE NOTICE 'Permisos asignados: %', v_permisos_count;
    RAISE NOTICE '========================================';
    RAISE NOTICE 'Credenciales de acceso:';
    RAISE NOTICE 'Email: superadmin@upeu.edu.pe';
    RAISE NOTICE 'Password: SuperAdmin2025!';
    RAISE NOTICE '========================================';
    RAISE NOTICE 'CAMBIAR PASSWORD EN PRIMER ACCESO';
    RAISE NOTICE '========================================';
END $$;
