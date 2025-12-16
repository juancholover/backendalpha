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

    @Transactional
    void onStart(@Observes StartupEvent ev) {
        LOG.info("Initializing Casbin Policies via Java Code...");

        // ==========================================
        // Definición de Políticas (Roles -> Permisos)
        // ==========================================

        // Base Permissions for Authorities (Using codes from TipoAutoridad)

        // DEC = Decano
        addPolicyIfMissing("DEC", "/api/v1/autoridades/*", "GET");
        addPolicyIfMissing("DEC", "/api/v1/programas-academicos/*", "*");
        addPolicyIfMissing("DEC", "/api/v1/profesores/*", "GET");
        addPolicyIfMissing("DEC", "/api/v1/estudiantes/*", "GET");

        // SG = Secretario General
        addPolicyIfMissing("SG", "/api/v1/autoridades/*", "*");
        addPolicyIfMissing("SG", "/api/v1/grados-titulos/*", "*");

        // DE = Director Escuela
        addPolicyIfMissing("DE", "/api/v1/estudiantes/*", "*");
        addPolicyIfMissing("DE", "/api/v1/profesores/*", "GET");
        addPolicyIfMissing("DE", "/api/v1/horarios/*", "*");

        // DP = Director Postgrado
        addPolicyIfMissing("DP", "/api/v1/programas-academicos/postgrado/*", "*");

        // DI = Director Investigacion
        addPolicyIfMissing("DI", "/api/v1/investigacion/*", "*");

        // DA = Director Administrativo
        addPolicyIfMissing("DA", "/api/v1/pagos/*", "*");
        addPolicyIfMissing("DA", "/api/v1/presupuestos/*", "*");

        // CA = Coordinador Académico
        addPolicyIfMissing("CA", "/api/v1/silabos/*", "*");
        addPolicyIfMissing("CA", "/api/v1/notas/*", "GET");

        // JD = Jefe Departamento
        addPolicyIfMissing("JD", "/api/v1/profesores/*", "GET");
        addPolicyIfMissing("JD", "/api/v1/carga-lectiva/*", "*");

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

        LOG.info("Casbin Policies Initialized.");
    }

    private void addPolicyIfMissing(String role, String path, String action) {
        // Opcional: Verificar antes de agregar para evitar duplicados en logs,
        // aunque Casbin suele manejar la idempotencia o retorno false si ya existe.
        casbinPolicyService.addPolicy(role, path, action);
    }
}
