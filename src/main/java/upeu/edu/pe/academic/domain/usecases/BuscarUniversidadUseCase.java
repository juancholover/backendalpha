package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.academic.domain.repositories.UniversidadRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;

@ApplicationScoped
public class BuscarUniversidadUseCase {

    private final UniversidadRepository repository;

    @Inject
    public BuscarUniversidadUseCase(UniversidadRepository repository) {
        this.repository = repository;
    }

    public Universidad findById(Long id) {
        return repository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Universidad no encontrada con ID: " + id));
    }

    public Universidad findByCodigo(String codigo) {
        return repository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Universidad no encontrada con código: " + codigo));
    }

    public Universidad findByDominio(String dominio) {
        return repository.findByDominio(dominio)
                .orElseThrow(() -> new ResourceNotFoundException("Universidad no encontrada con dominio: " + dominio));
    }

    public List<Universidad> findAllActive() {
        return repository.findAllActive();
    }

    public List<Universidad> findAll() {
        return repository.listAll();
    }

    public List<Universidad> search(String query) {
        return repository.search(query);
    }
}
