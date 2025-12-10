package upeu.edu.pe.core.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.core.domain.commands.CrearPersonaCommand;
import upeu.edu.pe.core.domain.entities.Persona;
import upeu.edu.pe.core.domain.repositories.PersonaRepository;
import upeu.edu.pe.shared.exceptions.DuplicateResourceException;

/**
 * Caso de Uso: Crear una nueva persona en el sistema.
 * 
 * Reglas de negocio:
 * - El número de documento debe ser único
 * - El email debe ser único (si se proporciona)
 */
@ApplicationScoped
public class CrearPersonaUseCase {

    @Inject
    PersonaRepository personaRepository;

    @Transactional
    public Persona execute(CrearPersonaCommand command) {

        // 1. Validar número de documento único
        if (personaRepository.existsByNumeroDocumento(command.numeroDocumento())) {
            throw new DuplicateResourceException("Persona", "numeroDocumento", command.numeroDocumento());
        }

        // 2. Validar email único (si se proporciona)
        if (command.email() != null && !command.email().isBlank()) {
            if (personaRepository.existsByEmail(command.email())) {
                throw new DuplicateResourceException("Persona", "email", command.email());
            }
        }

        // 3. Crear entidad
        Persona persona = new Persona();
        persona.setNombres(command.nombres());
        persona.setApellidoPaterno(command.apellidoPaterno());
        persona.setApellidoMaterno(command.apellidoMaterno());
        persona.setTipoDocumento(command.tipoDocumento());
        persona.setNumeroDocumento(command.numeroDocumento());
        persona.setEmail(command.email());
        persona.setTelefono(command.telefono());
        persona.setDireccion(command.direccion());
        persona.setGenero(command.genero());
        persona.setFechaNacimiento(command.fechaNacimiento());

        // 4. Persistir
        personaRepository.persist(persona);

        return persona;
    }
}
