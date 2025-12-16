package upeu.edu.pe.security.infrastructure.bootstrap;

import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
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
 * Las credenciales se configuran via variables de entorno.
 * Priority 3 = ejecuta después de MenuItems y RolMenus
 */
@ApplicationScoped
public class SuperAdminUserInitializer {

    private static final Logger LOG = Logger.getLogger(SuperAdminUserInitializer.class);

    @ConfigProperty(name = "app.superadmin.email", defaultValue = "admin@localhost")
    String superadminEmail;

    @ConfigProperty(name = "app.superadmin.password", defaultValue = "ChangeMe123!")
    String superadminPassword;

    @ConfigProperty(name = "app.superadmin.enabled", defaultValue = "true")
    boolean superadminEnabled;

    @Inject
    PersonaRepository personaRepository;

    @Inject
    AuthUsuarioRepository authUsuarioRepository;

    @Inject
    CasbinPolicyService casbinPolicyService;

    @Inject
    PasswordEncoder passwordEncoder;

    private static final String SUPERADMIN_DOC = "00000001";

    /**
     * Método principal sin @Transactional para poder manejar Casbin separadamente
     */
    void onStart(@Observes @Priority(3) StartupEvent ev) {
        if (!superadminEnabled) {
            LOG.info("SuperAdmin creation disabled via config.");
            return;
        }

        // Verificar si ya existe el usuario por email
        if (personaRepository.findByEmail(superadminEmail).isPresent()) {
            LOG.info("SuperAdmin user already exists (by email), skipping creation.");
            return;
        }

        // Verificar si ya existe persona con el documento reservado
        if (personaRepository.find("numeroDocumento", SUPERADMIN_DOC).firstResultOptional().isPresent()) {
            LOG.info("SuperAdmin persona already exists (by document), skipping creation.");
            return;
        }

        LOG.info("Creating SuperAdmin user...");

        // 1. Crear Persona y AuthUsuario en transacción separada
        createUserInTransaction();

        // 2. Asignar rol en Casbin (fuera de transacción JPA)
        try {
            casbinPolicyService.assignRole(superadminEmail, "SUPERADMIN");
            LOG.infof("✅ SuperAdmin user created: %s", superadminEmail);
            LOG.info("   ⚠️ CHANGE PASSWORD IN PRODUCTION!");
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
        persona.setNumeroDocumento("00000001");
        persona.setEmail(superadminEmail);
        persona.setTelefono("+51999999999");
        persona.setActive(true);
        personaRepository.persist(persona);

        // 2. Crear AuthUsuario
        AuthUsuario authUsuario = new AuthUsuario();
        authUsuario.setPersona(persona);
        authUsuario.setPasswordHash(passwordEncoder.encode(superadminPassword));
        authUsuario.setRolNombre("SUPERADMIN");
        authUsuario.setRequiereCambioPassword(true); // Forzar cambio en primer login
        authUsuario.setActive(true);
        authUsuarioRepository.persist(authUsuario);
    }
}
