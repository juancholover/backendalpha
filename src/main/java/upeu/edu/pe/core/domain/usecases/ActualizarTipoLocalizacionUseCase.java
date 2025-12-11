package upeu.edu.pe.core.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.core.domain.commands.ActualizarTipoLocalizacionCommand;
import upeu.edu.pe.core.domain.entities.TipoLocalizacion;
import upeu.edu.pe.core.domain.repositories.TipoLocalizacionRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Caso de Uso: Actualizar un tipo de localización.
 */
@ApplicationScoped
public class ActualizarTipoLocalizacionUseCase {

    @Inject
    TipoLocalizacionRepository tipoLocalizacionRepository;

    @Transactional
    public TipoLocalizacion execute(ActualizarTipoLocalizacionCommand command) {
        TipoLocalizacion tipoLocalizacion = tipoLocalizacionRepository.findByIdOptional(command.id())
                .orElseThrow(() -> new NotFoundException("Tipo de localización no encontrado con ID: " + command.id()));

        if (command.nombre() != null)
            tipoLocalizacion.setNombre(command.nombre());

        tipoLocalizacionRepository.persist(tipoLocalizacion);
        return tipoLocalizacion;
    }
}
