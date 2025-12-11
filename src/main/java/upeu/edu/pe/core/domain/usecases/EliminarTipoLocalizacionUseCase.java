package upeu.edu.pe.core.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.core.domain.entities.TipoLocalizacion;
import upeu.edu.pe.core.domain.repositories.TipoLocalizacionRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Caso de Uso: Eliminar un tipo de localización.
 */
@ApplicationScoped
public class EliminarTipoLocalizacionUseCase {

    @Inject
    TipoLocalizacionRepository tipoLocalizacionRepository;

    @Transactional
    public void execute(Long id) {
        TipoLocalizacion tipoLocalizacion = tipoLocalizacionRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Tipo de localización no encontrado con ID: " + id));
        tipoLocalizacionRepository.delete(tipoLocalizacion);
    }
}
