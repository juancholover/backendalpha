package upeu.edu.pe.security.infrastructure.bootstrap;

import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import upeu.edu.pe.core.domain.entities.Persona;
import upeu.edu.pe.core.domain.repositories.PersonaRepository;
import upeu.edu.pe.security.casbin.CasbinPolicyService;
import upeu.edu.pe.security.domain.entities.AuthUsuario;
import upeu.edu.pe.security.domain.repositories.AuthUsuarioRepository;
import upeu.edu.pe.security.infrastructure.utils.PasswordEncoder;

/**
 * Inicializa el usuario SuperAdmin al arrancar la aplicación.
 * Solo crea el usuario si no existe.
 * Credenciales por defecto: superadmin@upeu.edu.pe / SuperAdmin2025!
 * Priority 3 = ejecuta después de MenuItems y RolMenus
 */
@ApplicationScoped
public class SuperAdminUserInitializer {

    private static final Logger LOG = Logger.getLogger(SuperAdminUserInitializer.class);

    private static final String SUPERADMIN_EMAIL = "superadmin@upeu.edu.pe";
    private static final String SUPERADMIN_PASSWORD = "SuperAdmin2025!";
    private static final String SUPERADMIN_DOC = "00000001";

    @Inject
    PersonaRepository personaRepository;

    @Inject
    AuthUsuarioRepository authUsuarioRepository;

    @Inject
    CasbinPolicyService casbinPolicyService;

    @Inject
    PasswordEncoder passwordEncoder;

    /**
     * Método principal sin @Transactional para poder manejar Casbin separadamente
     */
    void onStart(@Observes @Priority(3) StartupEvent ev) {
        // Verificar si ya existe el usuario por email
        if (personaRepository.findByEmail(SUPERADMIN_EMAIL).isPresent()) {
            LOG.info("SuperAdmin user already exists, skipping creation.");
            return;
        }

        LOG.info("Creating SuperAdmin user...");

        // 1. Crear Persona y AuthUsuario en transacción separada
        createUserInTransaction();

        // 2. Asignar rol en Casbin (fuera de transacción JPA)
        try {
            casbinPolicyService.assignRole(SUPERADMIN_EMAIL, "SUPERADMIN");
            LOG.infof("✅ SuperAdmin user created: %s", SUPERADMIN_EMAIL);
            LOG.info("   Password: SuperAdmin2025! (CHANGE IN PRODUCTION!)");
        } catch (Exception e) {
            LOG.warnf("⚠️ SuperAdmin user created but Casbin role assignment failed: %s", e.getMessage());
            LOG.info("   You may need to manually add the role via Casbin API.");
        }
    }

    @Transactional
    void createUserInTransaction() {
        // 1. Crear Persona
        Persona persona = new Persona();
        persona.setNombres("Super");
        persona.setApellidoPaterno("Administrador");
        persona.setApellidoMaterno("Sistema");
        persona.setTipoDocumento("DNI");
        persona.setNumeroDocumento(SUPERADMIN_DOC);
        persona.setEmail(SUPERADMIN_EMAIL);
        persona.setTelefono("+51999999999");
        persona.setActive(true);
        personaRepository.persist(persona);

        // 2. Crear AuthUsuario
        AuthUsuario authUsuario = new AuthUsuario();
        authUsuario.setPersona(persona);
        authUsuario.setPasswordHash(passwordEncoder.encode(SUPERADMIN_PASSWORD));
        authUsuario.setRolNombre("SUPERADMIN");
        authUsuario.setRequiereCambioPassword(false);
        authUsuario.setActive(true);
        authUsuarioRepository.persist(authUsuario);
    }
}
