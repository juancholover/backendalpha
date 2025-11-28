package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.Persona;
import upeu.edu.pe.academic.domain.repositories.PersonaRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;

@ApplicationScoped
public class BuscarPersonaUseCase {

    private final PersonaRepository repository;

    @Inject
    public BuscarPersonaUseCase(PersonaRepository repository) {
        this.repository = repository;
    }

    public Persona findById(Long id) {
        return repository.findByIdOptional(id)
                .filter(Persona::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Persona", "id", id));
    }

    public Persona findByNumeroDocumento(String numeroDocumento) {
        return repository.findByNumeroDocumento(numeroDocumento)
                .orElseThrow(() -> new ResourceNotFoundException("Persona", "numeroDocumento", numeroDocumento));
    }

    public Persona findByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Persona", "email", email));
    }

    public List<Persona> findAllActive() {
        return repository.findAllActive();
    }

    public List<Persona> searchByNombres(String searchTerm) {
        return repository.searchByNombres(searchTerm);
    }
}
