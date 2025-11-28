package upeu.edu.pe.security.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.domain.entities.Persona;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.academic.domain.repositories.PersonaRepository;
import upeu.edu.pe.academic.domain.repositories.UniversidadRepository;
import upeu.edu.pe.security.domain.entities.AuthUsuario;
import upeu.edu.pe.security.domain.entities.Rol;
import upeu.edu.pe.security.domain.repositories.AuthUsuarioRepository;
import upeu.edu.pe.security.domain.repositories.RolRepository;
import upeu.edu.pe.security.infrastructure.utils.PasswordEncoder;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class CrearUsuarioUseCase {

    private final AuthUsuarioRepository authUsuarioRepository;
    private final PersonaRepository personaRepository;
    private final RolRepository rolRepository;
    private final UniversidadRepository universidadRepository;
    private final PasswordEncoder passwordEncoder;

    @Inject
    public CrearUsuarioUseCase(AuthUsuarioRepository authUsuarioRepository,
            PersonaRepository personaRepository,
            RolRepository rolRepository,
            UniversidadRepository universidadRepository,
            PasswordEncoder passwordEncoder) {
        this.authUsuarioRepository = authUsuarioRepository;
        this.personaRepository = personaRepository;
        this.rolRepository = rolRepository;
        this.universidadRepository = universidadRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthUsuario execute(String username, String password, String firstName, String lastName, String email,
            String phone, Long rolId, Long universidadId) {
        // Validar si ya existe usuario con ese username (email)
        if (authUsuarioRepository.existsByUsername(username)) {
            throw new BusinessException("El nombre de usuario ya está en uso");
        }

        // Buscar o crear Persona
        Persona persona = personaRepository.findByEmail(email)
                .orElseGet(() -> {
                    Universidad universidad = universidadRepository.findByIdOptional(universidadId)
                            .orElseThrow(() -> new ResourceNotFoundException("Universidad", "id", universidadId));

                    Persona newPersona = new Persona();
                    newPersona.setNombres(firstName);
                    newPersona.setApellidoPaterno(lastName);
                    newPersona.setEmail(new upeu.edu.pe.shared.domain.valueobjects.Email(email));
                    newPersona.setTelefono(phone);
                    newPersona.setNumeroDocumento("DNI_TEMP_" + System.currentTimeMillis()); // Placeholder
                    newPersona.setUniversidad(universidad);
                    personaRepository.persist(newPersona);
                    return newPersona;
                });

        // Validar si la persona ya tiene usuario
        if (authUsuarioRepository.existsByPersonaId(persona.getId())) {
            throw new BusinessException("La persona ya tiene un usuario asignado");
        }

        Rol rol = rolRepository.findByIdOptional(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", rolId));

        Universidad universidad = universidadRepository.findByIdOptional(universidadId)
                .orElseThrow(() -> new ResourceNotFoundException("Universidad", "id", universidadId));

        String passwordHash = passwordEncoder.encode(password);

        AuthUsuario authUsuario = AuthUsuario.crear(persona, rol, universidad, passwordHash);
        authUsuarioRepository.persist(authUsuario);

        return authUsuario;
    }
}
