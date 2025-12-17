package upeu.edu.pe.security.casbin;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CasbinPolicyInitializer {

    private static final Logger LOG = Logger.getLogger(CasbinPolicyInitializer.class);

    @Inject
    CasbinPolicyService casbinPolicyService;

    void onStart(@Observes StartupEvent ev) {
        LOG.info("Initializing Casbin Policies via Java Code...");

        // ==========================================
        // Definición de Políticas (Roles -> Permisos)
        // Los códigos deben coincidir con TipoAutoridad.codigo
        // ==========================================

        // ==========================================
        // Core Context Roles
        // ==========================================

        // ESTUDIANTE
        addPolicyIfMissing("ESTUDIANTE", "/api/v1/notas/mis-notas", "GET");
        addPolicyIfMissing("ESTUDIANTE", "/api/v1/matriculas/mis-matriculas", "GET");
        addPolicyIfMissing("ESTUDIANTE", "/api/v1/perfil/me", "GET");

        // EMPLEADO
        addPolicyIfMissing("EMPLEADO", "/api/v1/perfil/me", "GET");

        // PROFESOR
        addPolicyIfMissing("PROFESOR", "/api/v1/notas/*", "*");
        addPolicyIfMissing("PROFESOR", "/api/v1/silabos/*", "*");

        // ==========================================
        // ADMINISTRADORES Y AUTORIDADES MÁXIMAS
        // ==========================================

        // SUPERADMIN: Acceso total
        addPolicyIfMissing("SUPERADMIN", "/api/v1/*", "*");

        // ADMIN: Acceso total
        addPolicyIfMissing("ADMIN", "/api/v1/*", "*");

        // RECTOR: Acceso total
        addPolicyIfMissing("RECTOR", "/api/v1/*", "*");

        // ==========================================
        // PUBLIC ENDPOINTS (sin autenticación)
        // ==========================================
        addPolicyIfMissing("public", "/api/v1/auth/login", "POST");
        addPolicyIfMissing("public", "/api/v1/auth/refresh", "POST");
        addPolicyIfMissing("public", "/api/v1/auth/register", "POST");

        // ==========================================
        // AUTORIDADES UNIVERSITARIAS (Vicerrectores, Decanos, etc.)
        // ==========================================

        // VICERRECTOR_ACADEMICO
        addPolicyIfMissing("VICERRECTOR_ACADEMICO", "/api/v1/*", "GET");
        addPolicyIfMissing("VICERRECTOR_ACADEMICO", "/api/v1/programas-academicos/*", "*");
        addPolicyIfMissing("VICERRECTOR_ACADEMICO", "/api/v1/cursos/*", "*");
        addPolicyIfMissing("VICERRECTOR_ACADEMICO", "/api/v1/silabos/*", "*");
        addPolicyIfMissing("VICERRECTOR_ACADEMICO", "/api/v1/planes-academicos/*", "*");

        // VICERRECTOR_INVESTIGACION
        addPolicyIfMissing("VICERRECTOR_INVESTIGACION", "/api/v1/*", "GET");
        addPolicyIfMissing("VICERRECTOR_INVESTIGACION", "/api/v1/silabos/*", "*");
        addPolicyIfMissing("VICERRECTOR_INVESTIGACION", "/api/v1/profesores/*", "GET");

        // DECANO (nombre largo)
        addPolicyIfMissing("DECANO", "/api/v1/*", "GET");
        addPolicyIfMissing("DECANO", "/api/v1/programas-academicos/*", "*");
        addPolicyIfMissing("DECANO", "/api/v1/profesores/*", "*");
        addPolicyIfMissing("DECANO", "/api/v1/silabos/*", "*");
        addPolicyIfMissing("DECANO", "/api/v1/autoridades/*", "GET");

        // SECRETARIO_GENERAL
        addPolicyIfMissing("SECRETARIO_GENERAL", "/api/v1/*", "GET");
        addPolicyIfMissing("SECRETARIO_GENERAL", "/api/v1/personas/*", "*");
        addPolicyIfMissing("SECRETARIO_GENERAL", "/api/v1/empleados/*", "*");
        addPolicyIfMissing("SECRETARIO_GENERAL", "/api/v1/autoridades/*", "*");

        // DIRECTOR_ESCUELA
        addPolicyIfMissing("DIRECTOR_ESCUELA", "/api/v1/programas-academicos/*", "GET");
        addPolicyIfMissing("DIRECTOR_ESCUELA", "/api/v1/cursos/*", "*");
        addPolicyIfMissing("DIRECTOR_ESCUELA", "/api/v1/silabos/*", "*");
        addPolicyIfMissing("DIRECTOR_ESCUELA", "/api/v1/estudiantes/*", "GET");
        addPolicyIfMissing("DIRECTOR_ESCUELA", "/api/v1/profesores/*", "GET");
        addPolicyIfMissing("DIRECTOR_ESCUELA", "/api/v1/matriculas/*", "GET");

        // DIRECTOR_POSTGRADO
        addPolicyIfMissing("DIRECTOR_POSTGRADO", "/api/v1/programas-academicos/*", "GET");
        addPolicyIfMissing("DIRECTOR_POSTGRADO", "/api/v1/cursos/*", "*");
        addPolicyIfMissing("DIRECTOR_POSTGRADO", "/api/v1/silabos/*", "*");
        addPolicyIfMissing("DIRECTOR_POSTGRADO", "/api/v1/estudiantes/*", "GET");

        // DIRECTOR_INVESTIGACION
        addPolicyIfMissing("DIRECTOR_INVESTIGACION", "/api/v1/silabos/*", "GET");
        addPolicyIfMissing("DIRECTOR_INVESTIGACION", "/api/v1/profesores/*", "GET");

        // DIRECTOR_ADMINISTRATIVO
        addPolicyIfMissing("DIRECTOR_ADMINISTRATIVO", "/api/v1/empleados/*", "GET");
        addPolicyIfMissing("DIRECTOR_ADMINISTRATIVO", "/api/v1/localizaciones/*", "*");
        addPolicyIfMissing("DIRECTOR_ADMINISTRATIVO", "/api/v1/unidades-organizativas/*", "GET");

        // COORDINADOR_ACADEMICO
        addPolicyIfMissing("COORDINADOR_ACADEMICO", "/api/v1/cursos-ofertados/*", "*");
        addPolicyIfMissing("COORDINADOR_ACADEMICO", "/api/v1/horarios/*", "*");
        addPolicyIfMissing("COORDINADOR_ACADEMICO", "/api/v1/matriculas/*", "GET");
        addPolicyIfMissing("COORDINADOR_ACADEMICO", "/api/v1/silabos/*", "GET");

        // JEFE_DEPARTAMENTO
        addPolicyIfMissing("JEFE_DEPARTAMENTO", "/api/v1/profesores/*", "GET");
        addPolicyIfMissing("JEFE_DEPARTAMENTO", "/api/v1/cursos/*", "GET");
        addPolicyIfMissing("JEFE_DEPARTAMENTO", "/api/v1/silabos/*", "*");

        LOG.info("Casbin Policies Initialized.");
    }

    private void addPolicyIfMissing(String role, String path, String action) {
        // Opcional: Verificar antes de agregar para evitar duplicados en logs,
        // aunque Casbin suele manejar la idempotencia o retorno false si ya existe.
        casbinPolicyService.addPolicy(role, path, action);
    }
}
