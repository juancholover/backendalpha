package upeu.edu.pe.core.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.core.domain.commands.CrearTipoLocalizacionCommand;
import upeu.edu.pe.core.domain.entities.TipoLocalizacion;
import upeu.edu.pe.core.domain.repositories.TipoLocalizacionRepository;

/**
 * Caso de Uso: Crear un tipo de localización.
 */
@ApplicationScoped
public class CrearTipoLocalizacionUseCase {

    @Inject
    TipoLocalizacionRepository tipoLocalizacionRepository;

    @Transactional
    public TipoLocalizacion execute(CrearTipoLocalizacionCommand command) {
        TipoLocalizacion tipoLocalizacion = new TipoLocalizacion();
        tipoLocalizacion.setNombre(command.nombre());

        tipoLocalizacionRepository.persist(tipoLocalizacion);
        return tipoLocalizacion;
    }
}
