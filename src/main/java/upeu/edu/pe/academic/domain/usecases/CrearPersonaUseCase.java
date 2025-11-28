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
public class CrearPersonaUseCase {

    private final PersonaRepository personaRepository;
    private final UniversidadRepository universidadRepository;

    @Inject
    public CrearPersonaUseCase(PersonaRepository personaRepository, UniversidadRepository universidadRepository) {
        this.personaRepository = personaRepository;
        this.universidadRepository = universidadRepository;
    }

    public Persona execute(Long universidadId, String nombres, String apellidoPaterno, String apellidoMaterno,
            String tipoDocumento, String numeroDocumento, LocalDate fechaNacimiento,
            String genero, String estadoCivil, String direccion, String telefono,
            String celular, String email, String fotoUrl) {

        // Validar Universidad
        Universidad universidad = universidadRepository.findByIdOptional(universidadId)
                .orElseThrow(() -> new ResourceNotFoundException("Universidad", "id", universidadId));

        // Validar duplicados
        if (personaRepository.existsByNumeroDocumento(numeroDocumento)) {
            throw new DuplicateResourceException("Persona", "numeroDocumento", numeroDocumento);
        }

        if (email != null && personaRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Persona", "email", email);
        }

        // Crear entidad
        Persona persona = Persona.crear(universidad, nombres, apellidoPaterno, apellidoMaterno,
                tipoDocumento, numeroDocumento, fechaNacimiento,
                genero, estadoCivil, direccion, telefono,
                celular, email, fotoUrl);

        personaRepository.persist(persona);

        return persona;
    }
}
