package upeu.edu.pe.core.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.core.domain.entities.TipoUnidad;
import upeu.edu.pe.core.domain.repositories.TipoUnidadRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Caso de Uso: Eliminar un tipo de unidad organizativa.
 */
@ApplicationScoped
public class EliminarTipoUnidadUseCase {

    @Inject
    TipoUnidadRepository tipoUnidadRepository;

    @Transactional
    public void execute(Long id) {
        TipoUnidad tipoUnidad = tipoUnidadRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Tipo de unidad no encontrado con ID: " + id));
        tipoUnidadRepository.delete(tipoUnidad);
    }
}
