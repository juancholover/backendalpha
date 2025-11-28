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
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class ActualizarUsuarioUseCase {

    private final AuthUsuarioRepository authUsuarioRepository;
    private final PersonaRepository personaRepository;
    private final RolRepository rolRepository;
    private final UniversidadRepository universidadRepository;

    @Inject
    public ActualizarUsuarioUseCase(AuthUsuarioRepository authUsuarioRepository,
            PersonaRepository personaRepository,
            RolRepository rolRepository,
            UniversidadRepository universidadRepository) {
        this.authUsuarioRepository = authUsuarioRepository;
        this.personaRepository = personaRepository;
        this.rolRepository = rolRepository;
        this.universidadRepository = universidadRepository;
    }

    @Transactional
    public AuthUsuario execute(Long id, String firstName, String lastName, String email, String phone, Long rolId,
            Long universidadId) {
        AuthUsuario authUsuario = authUsuarioRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        Persona persona = authUsuario.getPersona();

        // Verificar si el email cambió y si ya existe
        if (!persona.getEmail().getValue().equals(email) && personaRepository.existsByEmail(email)) {
            throw new BusinessException("El email ya está en uso por otra persona");
        }

        persona.setNombres(firstName);
        persona.setApellidoPaterno(lastName);
        persona.setEmail(new upeu.edu.pe.shared.domain.valueobjects.Email(email));
        persona.setTelefono(phone);
        personaRepository.persist(persona);

        if (rolId != null) {
            Rol rol = rolRepository.findByIdOptional(rolId)
                    .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", rolId));
            authUsuario.setRol(rol);
        }

        if (universidadId != null) {
            Universidad universidad = universidadRepository.findByIdOptional(universidadId)
                    .orElseThrow(() -> new ResourceNotFoundException("Universidad", "id", universidadId));
            authUsuario.setUniversidad(universidad);
        }

        authUsuarioRepository.persist(authUsuario);
        return authUsuario;
    }
}
