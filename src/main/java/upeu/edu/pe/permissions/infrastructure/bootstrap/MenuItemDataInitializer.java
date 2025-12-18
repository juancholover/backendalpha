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

                LOG.info("Seeding MenuItem data (All modules)...");

                // ==========================================
                // ISLA: SUPERADMIN
                // ==========================================
                MenuItem superadmin = createIsla("SUPERADMIN", "Gestor del sistema", "Panel Técnico",
                                "/super-admin", "shield-alert", "#7C3AED", 0);

                createTarget(superadmin, "SA_DASHBOARD", "Dashboard",
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

                createTarget(superadmin, "SA_PERMISOS_IND", "Permisos Individuales",
                                "Asignar permisos temporales a usuarios", "/super-admin/permisos-individuales",
                                "user-check", 6);

                createTarget(superadmin, "SA_AUDITORIA", "Auditoría",
                                "Registro de actividades y cambios", "/super-admin/auditoria", "file-search", 7);

                MenuItem saCatalogos = createTarget(superadmin, "SA_CATALOGOS", "Catálogos",
                                "Tablas maestras y configuraciones", "/super-admin/catalogos", "list", 8);
                addApi(saCatalogos, "/api/v1/tipos-autoridad", "Tipos de autoridad", true, true, true, false);
                addApi(saCatalogos, "/api/v1/tipos-localizacion", "Tipos de localización", true, true, true, false);
                addApi(saCatalogos, "/api/v1/localizaciones", "Localizaciones", true, true, true, true);

                createTarget(superadmin, "SA_BACKUPS", "Backups y Logs",
                                "Respaldos del sistema y registros", "/super-admin/backups", "database", 9);

                // ==========================================
                // ISLA: GESTIÓN ACADÉMICA (Vice Rectorado)
                // ==========================================
                MenuItem gestionAcademica = createIsla("GESTION_ACADEMICA", "Gestión Académica",
                                "Módulo de gestión académica institucional (Vice Rectorado)",
                                "/vice-rectorado", "GraduationCap", "#6366F1", 1);

                MenuItem acadDashboard = createTarget(gestionAcademica, "ACAD_DASHBOARD", "Dashboard Académico",
                                "Vista general con KPIs académicos institucionales", "/vice-rectorado/dashboard",
                                "layout-dashboard", 1);
                addApi(acadDashboard, "/academico/dashboard", "Dashboard académico", true, false, false, false);

                MenuItem acadPeriodos = createTarget(gestionAcademica, "ACAD_PERIODOS", "Períodos Académicos",
                                "Gestión de semestres y ciclos académicos", "/vice-rectorado/periodos", "calendar", 2);
                addApi(acadPeriodos, "/academico/periodos", "CRUD períodos", true, true, true, true);

                MenuItem acadPlanes = createTarget(gestionAcademica, "ACAD_PLANES", "Planes de Estudio",
                                "Mallas curriculares y planes académicos", "/vice-rectorado/planes", "file-text", 3);
                addApi(acadPlanes, "/academico/planes", "CRUD planes", true, true, true, true);

                MenuItem acadCursos = createTarget(gestionAcademica, "ACAD_CURSOS", "Catálogo de Cursos",
                                "Base de datos de asignaturas institucionales", "/vice-rectorado/cursos", "book", 4);
                addApi(acadCursos, "/academico/cursos", "CRUD cursos", true, true, true, true);

                MenuItem acadOferta = createTarget(gestionAcademica, "ACAD_OFERTA", "Oferta Académica",
                                "Cursos ofertados por período académico", "/vice-rectorado/oferta", "layers", 5);
                addApi(acadOferta, "/academico/oferta", "CRUD oferta", true, true, true, true);

                MenuItem acadSilabos = createTarget(gestionAcademica, "ACAD_SILABOS", "Gestión de Sílabos",
                                "Sílabos institucionales", "/vice-rectorado/silabos", "file-check", 6);
                addApi(acadSilabos, "/academico/silabos", "CRUD sílabos", true, true, true, true);

                // ==========================================
                // ISLA: RECTORADO EJECUTIVO
                // ==========================================
                MenuItem rectorado = createIsla("RECTORADO_EJECUTIVO", "Rectorado Ejecutivo",
                                "Panel ejecutivo del Rectorado",
                                "/rector", "Crown", "#F59E0B", 2);

                MenuItem rectDashboard = createTarget(rectorado, "RECT_DASHBOARD", "Dashboard Ejecutivo",
                                "Indicadores institucionales de alto nivel", "/rector/dashboard", "layout-dashboard",
                                1);
                addApi(rectDashboard, "/rector/dashboard", "Dashboard rector", true, false, false, false);

                MenuItem rectUniversidad = createTarget(rectorado, "RECT_UNIVERSIDAD", "Datos Universidad",
                                "Información institucional y datos generales", "/rector/universidad", "building", 2);
                addApi(rectUniversidad, "/universidad", "Datos universidad", true, false, true, false);

                MenuItem rectEstructura = createTarget(rectorado, "RECT_ESTRUCTURA", "Estructura Organizacional",
                                "Facultades, escuelas y unidades", "/rector/unidades", "sitemap", 3);
                addApi(rectEstructura, "/unidades", "Estructura org", true, true, true, true);

                MenuItem rectProgramas = createTarget(rectorado, "RECT_PROGRAMAS", "Programas Académicos",
                                "Carreras y programas ofertados", "/rector/programas", "graduation-cap", 4);
                addApi(rectProgramas, "/programas", "CRUD programas", true, true, true, true);

                MenuItem rectAutoridades = createTarget(rectorado, "RECT_AUTORIDADES", "Autoridades",
                                "Directivos y autoridades institucionales", "/rector/autoridades", "users", 5);
                addApi(rectAutoridades, "/autoridades", "CRUD autoridades", true, true, true, true);

                MenuItem rectReportes = createTarget(rectorado, "RECT_REPORTES", "Reportes Gerenciales",
                                "Estadísticas e informes ejecutivos", "/rector/reportes", "bar-chart", 6);
                addApi(rectReportes, "/reportes/gerenciales", "Reportes ejecutivos", true, false, false, false);

                // ==========================================
                // ISLA: GESTIÓN DE FACULTAD (Decano)
                // ==========================================
                MenuItem gestionFacultad = createIsla("GESTION_FACULTAD", "Gestión de Facultad",
                                "Módulo de gestión para Decanato",
                                "/decano", "School", "#14B8A6", 3);

                MenuItem decDashboard = createTarget(gestionFacultad, "DEC_DASHBOARD", "Dashboard Facultad",
                                "Indicadores de facultad", "/decano/dashboard", "layout-dashboard", 1);
                addApi(decDashboard, "/facultad/dashboard", "Dashboard facultad", true, false, false, false);

                MenuItem decFacultad = createTarget(gestionFacultad, "DEC_FACULTAD", "Mi Facultad",
                                "Información de la facultad", "/decano/facultad", "building-2", 2);
                addApi(decFacultad, "/facultad", "Datos facultad", true, false, true, false);

                MenuItem decConsejo = createTarget(gestionFacultad, "DEC_CONSEJO", "Consejo de Facultad",
                                "Sesiones y acuerdos del consejo", "/decano/consejo", "users", 3);
                addApi(decConsejo, "/facultad/consejo", "Consejo facultad", true, true, true, true);

                MenuItem decEstudiantes = createTarget(gestionFacultad, "DEC_ESTUDIANTES", "Estudiantes Facultad",
                                "Alumnos matriculados por escuela", "/decano/estudiantes", "user", 4);
                addApi(decEstudiantes, "/facultad/estudiantes", "Estudiantes facultad", true, false, false, false);

                MenuItem decSilabos = createTarget(gestionFacultad, "DEC_SILABOS", "Aprobación de Sílabos",
                                "Flujo de aprobación de sílabos", "/decano/silabos", "file-check", 5);
                addApi(decSilabos, "/facultad/silabos", "Aprobación sílabos", true, false, true, false);

                MenuItem decDocentes = createTarget(gestionFacultad, "DEC_DOCENTES", "Docentes",
                                "Personal docente de la facultad", "/decano/docentes", "briefcase", 6);
                addApi(decDocentes, "/facultad/docentes", "Docentes facultad", true, true, true, true);

                MenuItem decReportes = createTarget(gestionFacultad, "DEC_REPORTES", "Reportes Facultad",
                                "Estadísticas de facultad", "/decano/reportes", "bar-chart", 7);
                addApi(decReportes, "/facultad/reportes", "Reportes facultad", true, false, false, false);

                // ==========================================
                // ISLA: SECRETARÍA GENERAL
                // ==========================================
                MenuItem secretaria = createIsla("SECRETARIA_GENERAL", "Secretaría General",
                                "Módulo de Secretaría General",
                                "/secretario", "ScrollText", "#64748B", 4);

                MenuItem secDashboard = createTarget(secretaria, "SEC_DASHBOARD", "Dashboard Secretaría",
                                "Indicadores de trámites", "/secretario/dashboard", "layout-dashboard", 1);
                addApi(secDashboard, "/secretaria/dashboard", "Dashboard secretaría", true, false, false, false);

                MenuItem secTramites = createTarget(secretaria, "SEC_TRAMITES", "Gestión de Trámites",
                                "Mesa de partes y trámites documentarios", "/secretario/tramites", "file-text", 2);
                addApi(secTramites, "/secretaria/tramites", "CRUD trámites", true, true, true, true);

                MenuItem secCertificaciones = createTarget(secretaria, "SEC_CERTIFICACIONES", "Certificaciones",
                                "Constancias y legalizaciones", "/secretario/certificaciones", "award", 3);
                addApi(secCertificaciones, "/secretaria/certificaciones", "Certificaciones", true, true, true, false);

                MenuItem secArchivo = createTarget(secretaria, "SEC_ARCHIVO", "Archivo Institucional",
                                "Documentos históricos", "/secretario/archivo", "archive", 4);
                addApi(secArchivo, "/secretaria/archivo", "Archivo institucional", true, true, true, false);

                MenuItem secAutoridades = createTarget(secretaria, "SEC_AUTORIDADES", "Autoridades",
                                "Registro de cargos institucionales", "/secretario/autoridades", "users", 5);
                addApi(secAutoridades, "/secretaria/autoridades", "Autoridades", true, true, true, true);

                MenuItem secGraduados = createTarget(secretaria, "SEC_GRADUADOS", "Graduados",
                                "Egresados y titulados", "/secretario/graduados", "graduation-cap", 6);
                addApi(secGraduados, "/secretaria/graduados", "Graduados", true, true, true, false);

                MenuItem secReportes = createTarget(secretaria, "SEC_REPORTES", "Reportes Secretaría",
                                "Estadísticas tramitarias", "/secretario/reportes", "bar-chart", 7);
                addApi(secReportes, "/secretaria/reportes", "Reportes secretaría", true, false, false, false);

                // ==========================================
                // ISLA: DIRECCIÓN DE ESCUELA
                // ==========================================
                MenuItem direccion = createIsla("DIRECCION_ESCUELA", "Dirección de Escuela",
                                "Módulo de gestión de escuela profesional",
                                "/director", "School2", "#0EA5E9", 5);

                MenuItem dirDashboard = createTarget(direccion, "DIR_DASHBOARD", "Dashboard Escuela",
                                "Indicadores del programa", "/director/dashboard", "layout-dashboard", 1);
                addApi(dirDashboard, "/escuela/dashboard", "Dashboard escuela", true, false, false, false);

                MenuItem dirPrograma = createTarget(direccion, "DIR_PROGRAMA", "Mi Programa",
                                "Información de la carrera", "/director/programa", "file-text", 2);
                addApi(dirPrograma, "/escuela/programa", "Datos programa", true, false, true, false);

                MenuItem dirConsejo = createTarget(direccion, "DIR_CONSEJO", "Consejo de Escuela",
                                "Sesiones y acuerdos del consejo", "/director/consejo", "users", 3);
                addApi(dirConsejo, "/escuela/consejo", "Consejo escuela", true, true, true, true);

                MenuItem dirEstudiantes = createTarget(direccion, "DIR_ESTUDIANTES", "Estudiantes Programa",
                                "Alumnos de la carrera", "/director/estudiantes", "user", 4);
                addApi(dirEstudiantes, "/escuela/estudiantes", "Estudiantes escuela", true, false, false, false);

                MenuItem dirDocentes = createTarget(direccion, "DIR_DOCENTES", "Docentes Programa",
                                "Profesores asignados", "/director/docentes", "briefcase", 5);
                addApi(dirDocentes, "/escuela/docentes", "Docentes escuela", true, true, true, false);

                MenuItem dirSilabos = createTarget(direccion, "DIR_SILABOS", "Sílabos Programa",
                                "Contenidos de cursos", "/director/silabos", "file-check", 6);
                addApi(dirSilabos, "/escuela/silabos", "Sílabos escuela", true, true, true, false);

                MenuItem dirReportes = createTarget(direccion, "DIR_REPORTES", "Reportes Programa",
                                "Estadísticas de escuela", "/director/reportes", "bar-chart", 7);
                addApi(dirReportes, "/escuela/reportes", "Reportes escuela", true, false, false, false);

                // ==========================================
                // ISLA: PORTAL ESTUDIANTIL
                // ==========================================
                MenuItem estudiante = createIsla("PORTAL_ESTUDIANTIL", "Portal Estudiantil",
                                "Módulo de autoservicio estudiantil",
                                "/estudiante", "GraduationCap", "#8B5CF6", 6);

                MenuItem estDashboard = createTarget(estudiante, "EST_DASHBOARD", "Dashboard Estudiante",
                                "Vista general del alumno", "/estudiante/dashboard", "layout-dashboard", 1);
                addApi(estDashboard, "/estudiante/dashboard", "Dashboard estudiante", true, false, false, false);

                MenuItem estPerfil = createTarget(estudiante, "EST_PERFIL", "Perfil Académico",
                                "Datos personales y académicos", "/estudiante/perfil", "user", 2);
                addApi(estPerfil, "/estudiante/perfil", "Perfil estudiante", true, false, true, false);

                MenuItem estMatricula = createTarget(estudiante, "EST_MATRICULA", "Matrícula",
                                "Inscripción de cursos", "/estudiante/matricula", "edit", 3);
                addApi(estMatricula, "/estudiante/matricula", "Matrícula", true, true, true, true);

                MenuItem estCursos = createTarget(estudiante, "EST_CURSOS", "Mis Cursos",
                                "Cursos matriculados actualmente", "/estudiante/cursos", "book", 4);
                addApi(estCursos, "/estudiante/cursos", "Cursos estudiante", true, false, false, false);

                MenuItem estHistorial = createTarget(estudiante, "EST_HISTORIAL", "Historial Académico",
                                "Record de notas completo", "/estudiante/historial", "file-text", 5);
                addApi(estHistorial, "/estudiante/historial", "Historial académico", true, false, false, false);

                MenuItem estCuenta = createTarget(estudiante, "EST_CUENTA", "Estado de Cuenta",
                                "Pagos y deudas pendientes", "/estudiante/cuenta", "credit-card", 6);
                addApi(estCuenta, "/estudiante/cuenta", "Estado cuenta", true, false, false, false);

                MenuItem estHorario = createTarget(estudiante, "EST_HORARIO", "Horario Semanal",
                                "Clases semanales", "/estudiante/horario", "calendar", 7);
                addApi(estHorario, "/estudiante/horario", "Horario clases", true, false, false, false);

                LOG.info("MenuItem data seeded successfully (7 islas + 49 targets).");
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
        }
}
