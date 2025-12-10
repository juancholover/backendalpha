package upeu.edu.pe.core.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.core.domain.commands.ActualizarPersonaCommand;
import upeu.edu.pe.core.domain.entities.Persona;
import upeu.edu.pe.core.domain.repositories.PersonaRepository;
import upeu.edu.pe.shared.exceptions.DuplicateResourceException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Caso de Uso: Actualizar una persona existente.
 * 
 * Reglas de negocio:
 * - La persona debe existir
 * - El número de documento debe ser único (excluyendo la persona actual)
 * - El email debe ser único (excluyendo la persona actual)
 */
@ApplicationScoped
public class ActualizarPersonaUseCase {

    @Inject
    PersonaRepository personaRepository;

    @Transactional
    public Persona execute(ActualizarPersonaCommand command) {

        // 1. Buscar persona existente
        Persona persona = personaRepository.findByIdOptional(command.id())
                .filter(Persona::getActive)
                .orElseThrow(() -> new NotFoundException("Persona no encontrada con ID: " + command.id()));

        // 2. Validar número de documento único
        if (personaRepository.existsByNumeroDocumentoAndIdNot(command.numeroDocumento(), command.id())) {
            throw new DuplicateResourceException("Persona", "numeroDocumento", command.numeroDocumento());
        }

        // 3. Validar email único
        if (command.email() != null && !command.email().isBlank()) {
            if (personaRepository.existsByEmailAndIdNot(command.email(), command.id())) {
                throw new DuplicateResourceException("Persona", "email", command.email());
            }
        }

        // 4. Actualizar entidad
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

        // 5. Persistir
        personaRepository.persist(persona);

        return persona;
    }
}
