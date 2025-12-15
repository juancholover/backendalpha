package upeu.edu.pe.security.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.core.domain.entities.Persona;
import upeu.edu.pe.people.domain.entities.Autoridad;
import upeu.edu.pe.people.domain.entities.TipoAutoridad;
import upeu.edu.pe.people.domain.repositories.AutoridadRepository;
import upeu.edu.pe.people.domain.repositories.EmpleadoRepository;
import upeu.edu.pe.people.domain.repositories.EstudianteRepository;
import upeu.edu.pe.people.domain.repositories.ProfesorRepository;
import upeu.edu.pe.security.casbin.CasbinPolicyService;
import upeu.edu.pe.security.domain.entities.AuthUsuario;
import upeu.edu.pe.security.domain.repositories.AuthUsuarioRepository;
import upeu.edu.pe.security.infrastructure.utils.PasswordEncoder;

import java.util.List;

@ApplicationScoped
public class RoleSyncService {

    @Inject
    CasbinPolicyService casbinService;

    @Inject
    AuthUsuarioRepository authUsuarioRepository;

    @Inject
    PasswordEncoder passwordEncoder;

    @Inject
    EstudianteRepository estudianteRepository;

    @Inject
    EmpleadoRepository empleadoRepository;

    @Inject
    ProfesorRepository profesorRepository;

    @Inject
    AutoridadRepository autoridadRepository;

    /**
     * Asegura que exista un usuario para la persona.
     * Si no existe, lo crea con password = DNI.
     */
    @Transactional
    public AuthUsuario ensureAuthUsuario(Persona persona) {
        if (persona.getEmail() == null || persona.getEmail().isEmpty()) {
            return null; // No se puede crear usuario sin email
        }

        return authUsuarioRepository.findByUsername(persona.getEmail())
                .orElseGet(() -> createAuthUsuario(persona));
    }

    private AuthUsuario createAuthUsuario(Persona persona) {
        AuthUsuario usuario = new AuthUsuario();
        usuario.setPersona(persona);

        // Password inicial = Número de Documento (DNI)
        // Si no tiene DNI, fallback a "123456" (caso borde, aunque DNI es required en
        // Persona)
        String rawPassword = persona.getNumeroDocumento() != null ? persona.getNumeroDocumento() : "123456";
        usuario.setPasswordHash(passwordEncoder.encode(rawPassword));

        // Rol legado (solo referencia)
        usuario.setRolNombre("USER");

        // Configuración de seguridad
        usuario.setRequiereCambioPassword(true); // Forzar cambio al primer login
        usuario.setIntentosFallidos(0);
        usuario.setActive(true);

        authUsuarioRepository.persist(usuario);
        System.out.println("✅ AuthUsuario creado para: " + persona.getEmail());

        return usuario;
    }

    /**
     * Asigna un rol a un usuario en Casbin por email
     */
    public void assignRole(String email, String role) {
        if (email == null || role == null)
            return;
        casbinService.assignRole(email, role);
    }

    /**
     * Remueve un rol de un usuario en Casbin por email
     */
    public void removeRole(String email, String role) {
        if (email == null || role == null)
            return;
        casbinService.removeRole(email, role);
    }

    /**
     * Sincroniza todos los roles de una persona basado en las tablas.
     * Útil para migración, reparación y creación inicial.
     */
    @Transactional
    public void syncAllRolesForPersona(Persona persona) {
        String email = persona.getEmail();
        if (email == null || email.isEmpty())
            return;

        // 1. Asegurar que tenga usuario
        ensureAuthUsuario(persona);

        // 2. Rol ESTUDIANTE
        // Usamos count() para ser genéricos con PanacheRepository
        if (estudianteRepository.count("persona.id = ?1 and active = true", persona.getId()) > 0) {
            assignRole(email, "ESTUDIANTE");
        }

        // 3. Rol EMPLEADO y PROFESOR
        empleadoRepository.findByPersona(persona.getId()).ifPresent(empleado -> {
            assignRole(email, "EMPLEADO");

            // Verificar si es profesor (buscando por empleado_id)
            if (profesorRepository.count("empleado.id = ?1 and active = true", empleado.getId()) > 0) {
                assignRole(email, "PROFESOR");
            }
        });

        // 4. Roles Autoridad Vigentes
        // Obtenemos todas y filtramos vigentes
        List<Autoridad> autoridades = autoridadRepository.findByPersonaId(persona.getId());
        for (Autoridad autoridad : autoridades) {
            if (Boolean.TRUE.equals(autoridad.getEsVigente()) && Boolean.TRUE.equals(autoridad.getActive())) {
                TipoAutoridad tipo = autoridad.getTipoAutoridad();
                if (tipo != null && tipo.getCodigo() != null) {
                    assignRole(email, tipo.getCodigo());
                }
            }
        }

        System.out.println("✅ Roles sincronizados para: " + email);
    }
}
