package upeu.edu.pe.core.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.core.domain.entities.Universidad;
import upeu.edu.pe.core.domain.repositories.UniversidadRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Caso de Uso: Eliminar (lógicamente) una universidad.
 */
@ApplicationScoped
public class EliminarUniversidadUseCase {

    @Inject
    UniversidadRepository universidadRepository;

    @Transactional
    public void execute(Long id) {
        Universidad universidad = universidadRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Universidad no encontrada con ID: " + id));

        universidad.setActive(false);
        universidadRepository.persist(universidad);
    }
}
