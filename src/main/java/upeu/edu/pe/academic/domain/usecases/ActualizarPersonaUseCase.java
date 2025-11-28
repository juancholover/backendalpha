package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.Persona;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.academic.domain.repositories.PersonaRepository;
import upeu.edu.pe.academic.domain.repositories.UniversidadRepository;
import upeu.edu.pe.shared.exceptions.DuplicateResourceException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.time.LocalDate;

@ApplicationScoped
public class ActualizarPersonaUseCase {

    private final PersonaRepository personaRepository;
    private final UniversidadRepository universidadRepository;

    @Inject
    public ActualizarPersonaUseCase(PersonaRepository personaRepository, UniversidadRepository universidadRepository) {
        this.personaRepository = personaRepository;
        this.universidadRepository = universidadRepository;
    }

    public Persona execute(Long id, Long universidadId, String nombres, String apellidoPaterno, String apellidoMaterno,
            String tipoDocumento, String numeroDocumento, LocalDate fechaNacimiento,
            String genero, String estadoCivil, String direccion, String telefono,
            String celular, String email, String fotoUrl) {

        Persona persona = personaRepository.findByIdOptional(id)
                .filter(Persona::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Persona", "id", id));

        // Validar Universidad si cambió
        if (!persona.getUniversidad().getId().equals(universidadId)) {
            Universidad universidad = universidadRepository.findByIdOptional(universidadId)
                    .orElseThrow(() -> new ResourceNotFoundException("Universidad", "id", universidadId));
            persona.setUniversidad(universidad);
        }

        // Validar duplicados
        if (!persona.getNumeroDocumento().equals(numeroDocumento) &&
                personaRepository.existsByNumeroDocumentoAndIdNot(numeroDocumento, id)) {
            throw new DuplicateResourceException("Persona", "numeroDocumento", numeroDocumento);
        }

        if (email != null && (persona.getEmail() == null || !email.equals(persona.getEmail().getValue())) &&
                personaRepository.existsByEmailAndIdNot(email, id)) {
            throw new DuplicateResourceException("Persona", "email", email);
        }

        persona.setApellidoMaterno(apellidoMaterno);
        persona.setTipoDocumento(tipoDocumento);
        persona.setNumeroDocumento(numeroDocumento);
        persona.setFechaNacimiento(fechaNacimiento);
        persona.setGenero(genero);
        persona.setEstadoCivil(estadoCivil);
        persona.setDireccion(direccion);
        persona.setTelefono(telefono);
        persona.setCelular(celular);
        if (email != null && !email.isEmpty()) {
            persona.setEmail(new upeu.edu.pe.shared.domain.valueobjects.Email(email));
        }
        persona.setFotoUrl(fotoUrl);

        personaRepository.persist(persona);

        return persona;
    }
}
