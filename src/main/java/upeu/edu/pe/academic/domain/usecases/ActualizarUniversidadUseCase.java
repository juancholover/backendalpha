package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.academic.domain.repositories.UniversidadRepository;
import upeu.edu.pe.shared.exceptions.DuplicateResourceException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class ActualizarUniversidadUseCase {

    private final UniversidadRepository repository;

    @Inject
    public ActualizarUniversidadUseCase(UniversidadRepository repository) {
        this.repository = repository;
    }

    public Universidad execute(Long id, String codigo, String nombre, String ruc, String tipo, String dominio) {
        Universidad universidad = repository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Universidad no encontrada con ID: " + id));

        // Validar unicidad del código si cambió
        if (!universidad.getCodigo().equals(codigo) && repository.existsByCodigo(codigo)) {
            throw new DuplicateResourceException("Universidad", "codigo", codigo);
        }

        // Validar unicidad del dominio si cambió
        if (dominio != null && !dominio.equals(universidad.getDominio()) && repository.existsByDominio(dominio)) {
            throw new DuplicateResourceException("Universidad", "dominio", dominio);
        }

        // Validar unicidad del RUC si cambió
        return universidad;
    }
}
