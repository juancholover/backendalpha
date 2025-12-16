package upeu.edu.pe.permissions.infrastructure.bootstrap;

import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import upeu.edu.pe.permissions.domain.entities.ApiPermiso;
import upeu.edu.pe.permissions.domain.entities.MenuItem;
import upeu.edu.pe.permissions.domain.repositories.MenuItemRepository;

/**
 * Inicializa los datos de menú (Islas, Sidebar-Targets y APIs) al arrancar la
 * aplicación.
 * Solo inserta datos si la tabla está vacía, evitando duplicados.
 * Priority 1 = ejecuta primero
 */
@ApplicationScoped
public class MenuItemDataInitializer {

        private static final Logger LOG = Logger.getLogger(MenuItemDataInitializer.class);

        @Inject
        MenuItemRepository menuItemRepository;

        @Transactional
        void onStart(@Observes @Priority(1) StartupEvent ev) {
                if (menuItemRepository.count() > 0) {
                        LOG.info("MenuItems already exist, skipping initialization.");
                        return;
                }

                LOG.info("Seeding MenuItem data (Islas, Targets, APIs)...");

                // ==========================================
                // ISLAS (Módulos principales)
                // ==========================================

                MenuItem superadmin = createIsla("SUPERADMIN", "Gestor del sistema", "Panel Técnico",
                                "/super-admin", "shield-alert", "#7C3AED", 0);

                MenuItem admin = createIsla("ADMIN", "Super Admin", "Panel Técnico",
                                "/admin/dashboard", "shield", "#10B981", 1);

                MenuItem rector = createIsla("RECTOR", "Rector", "Gestión y supervisión universitaria",
                                "/rector/dashboard", "crown", "#8B0000", 2);

                MenuItem decano = createIsla("DECANO", "Decano", "Gestión de facultad",
                                "/decano/dashboard", "building-2", "#1E40AF", 3);

                MenuItem profesor = createIsla("PROFESOR", "Profesor", "Gestión académica docente",
                                "/profesor/dashboard", "graduation-cap", "#059669", 4);

                MenuItem estudiante = createIsla("ESTUDIANTE", "Estudiante", "Portal del estudiante",
                                "/estudiante/dashboard", "user", "#7C3AED", 5);

                // ==========================================
                // TARGETS PARA SUPERADMIN
                // ==========================================
                MenuItem saDashboard = createTarget(superadmin, "SA_DASHBOARD", "Dashboard",
                                "Panel principal con métricas del sistema", "/super-admin/dashboard",
                                "layout-dashboard", 1);

                MenuItem saInstitucion = createTarget(superadmin, "SA_INSTITUCION", "Institución",
                                "Gestión de universidades del sistema", "/super-admin/institucion", "building-2", 2);
                addApi(saInstitucion, "/api/v1/universidades", "Gestión de universidades", true, true, true, true);

                MenuItem saOrganizacion = createTarget(superadmin, "SA_ORGANIZACION", "Organización",
                                "Estructura organizativa y unidades", "/super-admin/organizacion", "sitemap", 3);
                addApi(saOrganizacion, "/api/v1/unidades-organizativas", "Unidades organizativas", true, true, true,
                                true);
                addApi(saOrganizacion, "/api/v1/tipos-unidad", "Tipos de unidad organizativa", true, true, true, false);

                MenuItem saUsuarios = createTarget(superadmin, "SA_USUARIOS", "Usuarios",
                                "Gestión de usuarios del sistema", "/super-admin/usuarios", "users", 4);
                addApi(saUsuarios, "/api/v1/personas", "Gestión de personas", true, true, true, true);
                addApi(saUsuarios, "/api/v1/auth/usuarios", "Gestión de usuarios auth", true, true, true, true);

                MenuItem saRoles = createTarget(superadmin, "SA_ROLES", "Roles y Permisos",
                                "Configuración de roles y permisos del sistema", "/super-admin/roles", "shield-check",
                                5);
                addApi(saRoles, "/api/v1/menu/islas", "Gestión de islas", true, true, true, true);
                addApi(saRoles, "/api/v1/menu/sidebar-targets", "Gestión de sidebar-targets", true, true, true, true);
                addApi(saRoles, "/api/v1/menu/apis", "Gestión de APIs", true, true, true, true);

                MenuItem saPermisosInd = createTarget(superadmin, "SA_PERMISOS_IND", "Permisos Individuales",
                                "Asignar permisos temporales a usuarios", "/super-admin/permisos-individuales",
                                "user-check", 6);

                MenuItem saAuditoria = createTarget(superadmin, "SA_AUDITORIA", "Auditoría",
                                "Registro de actividades y cambios", "/super-admin/auditoria", "file-search", 7);

                MenuItem saCatalogos = createTarget(superadmin, "SA_CATALOGOS", "Catálogos",
                                "Tablas maestras y configuraciones", "/super-admin/catalogos", "list", 8);
                addApi(saCatalogos, "/api/v1/tipos-autoridad", "Tipos de autoridad", true, true, true, false);
                addApi(saCatalogos, "/api/v1/tipos-localizacion", "Tipos de localización", true, true, true, false);
                addApi(saCatalogos, "/api/v1/localizaciones", "Localizaciones", true, true, true, true);

                MenuItem saBackups = createTarget(superadmin, "SA_BACKUPS", "Backups y Logs",
                                "Respaldos del sistema y registros", "/super-admin/backups", "database", 9);

                // ==========================================
                // TARGETS PARA ADMIN (copia simplificada)
                // ==========================================
                MenuItem adminDashboard = createTarget(admin, "ADMIN_DASHBOARD", "Dashboard",
                                "Panel principal con métricas del sistema", "/admin/dashboard", "layout-dashboard", 1);

                MenuItem adminInstitucion = createTarget(admin, "ADMIN_INSTITUCION", "Institución",
                                "Gestión de universidades del sistema", "/admin/institucion", "building-2", 2);
                addApi(adminInstitucion, "/api/v1/universidades", "Gestión de universidades", true, true, true, true);

                MenuItem adminOrganizacion = createTarget(admin, "ADMIN_ORGANIZACION", "Organización",
                                "Estructura organizativa y unidades", "/admin/organizacion", "sitemap", 3);
                addApi(adminOrganizacion, "/api/v1/unidades-organizativas", "Unidades organizativas", true, true, true,
                                true);
                addApi(adminOrganizacion, "/api/v1/tipos-unidad", "Tipos de unidad", true, true, true, false);

                MenuItem adminUsuarios = createTarget(admin, "ADMIN_USUARIOS", "Usuarios",
                                "Gestión de usuarios del sistema", "/admin/usuarios", "users", 4);
                addApi(adminUsuarios, "/api/v1/personas", "Gestión de personas", true, true, true, true);

                MenuItem adminRoles = createTarget(admin, "ADMIN_ROLES", "Roles y Permisos",
                                "Configuración de roles y permisos", "/admin/roles", "shield-check", 5);

                MenuItem adminPermisosInd = createTarget(admin, "ADMIN_PERMISOS_IND", "Permisos Individuales",
                                "Asignar permisos temporales a usuarios", "/admin/permisos-individuales", "user-check",
                                6);

                MenuItem adminAuditoria = createTarget(admin, "ADMIN_AUDITORIA", "Auditoría",
                                "Registro de actividades y cambios", "/admin/auditoria", "file-search", 7);

                MenuItem adminCatalogos = createTarget(admin, "ADMIN_CATALOGOS", "Catálogos",
                                "Tablas maestras y configuraciones", "/admin/catalogos", "list", 8);
                addApi(adminCatalogos, "/api/v1/tipos-autoridad", "Tipos de autoridad", true, true, true, false);
                addApi(adminCatalogos, "/api/v1/tipos-localizacion", "Tipos de localización", true, true, true, false);
                addApi(adminCatalogos, "/api/v1/localizaciones", "Localizaciones", true, true, true, true);

                MenuItem adminBackups = createTarget(admin, "ADMIN_BACKUPS", "Backups y Logs",
                                "Respaldos del sistema y registros", "/admin/backups", "database", 9);

                // ==========================================
                // TARGETS PARA PROFESOR
                // ==========================================
                MenuItem misCursos = createTarget(profesor, "MIS_CURSOS", "Mis Cursos",
                                "Cursos asignados", "/profesor/cursos", "book", 1);

                MenuItem calificaciones = createTarget(profesor, "CALIFICACIONES", "Calificaciones",
                                "Registro de notas", "/profesor/calificaciones", "edit", 2);

                MenuItem asistencia = createTarget(profesor, "ASISTENCIA", "Asistencia",
                                "Control de asistencia", "/profesor/asistencia", "clipboard-check", 3);

                LOG.info("MenuItem data seeded successfully.");
        }

        private MenuItem createIsla(String codigo, String nombre, String descripcion,
                        String rutaFrontend, String icono, String color, Integer orden) {
                MenuItem isla = new MenuItem();
                isla.setCodigo(codigo);
                isla.setNombre(nombre);
                isla.setDescripcion(descripcion);
                isla.setRutaFrontend(rutaFrontend);
                isla.setIcono(icono);
                isla.setColor(color);
                isla.setOrden(orden);
                isla.setActive(true);
                menuItemRepository.persist(isla);
                return isla;
        }

        private MenuItem createTarget(MenuItem padre, String codigo, String nombre,
                        String descripcion, String rutaFrontend, String icono, Integer orden) {
                MenuItem target = new MenuItem();
                target.setPadre(padre);
                target.setCodigo(codigo);
                target.setNombre(nombre);
                target.setDescripcion(descripcion);
                target.setRutaFrontend(rutaFrontend);
                target.setIcono(icono);
                target.setOrden(orden);
                target.setActive(true);
                menuItemRepository.persist(target);
                return target;
        }

        private void addApi(MenuItem menuItem, String apiBase, String descripcion,
                        boolean get, boolean post, boolean put, boolean delete) {
                ApiPermiso api = new ApiPermiso();
                api.setMenuItem(menuItem);
                api.setApiBase(apiBase);
                api.setDescripcion(descripcion);
                api.setPuedeGet(get);
                api.setPuedePost(post);
                api.setPuedePut(put);
                api.setPuedeDelete(delete);
                api.setActive(true);
                menuItem.getApis().add(api);
                // ApiPermiso se persiste en cascada vía MenuItem
        }
}
