package upeu.edu.pe.core.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.core.domain.commands.CrearTipoUnidadCommand;
import upeu.edu.pe.core.domain.entities.TipoUnidad;
import upeu.edu.pe.core.domain.repositories.TipoUnidadRepository;

/**
 * Caso de Uso: Crear un tipo de unidad organizativa.
 */
@ApplicationScoped
public class CrearTipoUnidadUseCase {

    @Inject
    TipoUnidadRepository tipoUnidadRepository;

    @Transactional
    public TipoUnidad execute(CrearTipoUnidadCommand command) {
        TipoUnidad tipoUnidad = new TipoUnidad();
        tipoUnidad.setNombre(command.nombre());
        tipoUnidad.setDescripcion(command.descripcion());
        tipoUnidad.setNivel(command.nivel());

        tipoUnidadRepository.persist(tipoUnidad);
        return tipoUnidad;
    }
}
