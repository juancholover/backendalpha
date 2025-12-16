-- =====================================================
-- V026__fix_superadmin_sidebar_names.sql
-- Corregir nombres de sidebar-targets para SUPERADMIN
-- =====================================================

-- Actualizar nombre de la isla SUPERADMIN
UPDATE menu_item SET 
    nombre = 'Super Admin',
    descripcion = 'Panel Técnico',
    ruta_frontend = '/super-admin'
WHERE codigo = 'SUPERADMIN';

-- Actualizar rutas y nombres de sidebar-targets
UPDATE menu_item SET 
    nombre = 'Dashboard',
    ruta_frontend = '/super-admin/dashboard'
WHERE codigo = 'SA_DASHBOARD';

UPDATE menu_item SET 
    nombre = 'Institución',
    ruta_frontend = '/super-admin/institucion'
WHERE codigo = 'SA_INSTITUCION';

UPDATE menu_item SET 
    nombre = 'Organización',
    ruta_frontend = '/super-admin/organizacion'
WHERE codigo = 'SA_ORGANIZACION';

UPDATE menu_item SET 
    nombre = 'Usuarios',
    ruta_frontend = '/super-admin/usuarios'
WHERE codigo = 'SA_USUARIOS';

UPDATE menu_item SET 
    nombre = 'Roles y Permisos',
    ruta_frontend = '/super-admin/roles'
WHERE codigo = 'SA_ROLES';

UPDATE menu_item SET 
    nombre = 'Permisos Individuales',
    ruta_frontend = '/super-admin/permisos-individuales'
WHERE codigo = 'SA_PERMISOS_IND';

UPDATE menu_item SET 
    nombre = 'Auditoría',
    ruta_frontend = '/super-admin/auditoria'
WHERE codigo = 'SA_AUDITORIA';

UPDATE menu_item SET 
    nombre = 'Catálogos',
    ruta_frontend = '/super-admin/catalogos'
WHERE codigo = 'SA_CATALOGOS';

UPDATE menu_item SET 
    nombre = 'Backups y Logs',
    ruta_frontend = '/super-admin/backups'
WHERE codigo = 'SA_BACKUPS';

-- Remover el rol ADMIN del superadmin (solo debe tener SUPERADMIN)
DELETE FROM casbin_rule 
WHERE ptype = 'g' AND v0 = 'superadmin@upeu.edu.pe' AND v1 = 'ADMIN';
