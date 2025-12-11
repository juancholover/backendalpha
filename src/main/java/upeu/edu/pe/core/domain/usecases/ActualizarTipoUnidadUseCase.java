package upeu.edu.pe.core.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.core.domain.commands.ActualizarTipoUnidadCommand;
import upeu.edu.pe.core.domain.entities.TipoUnidad;
import upeu.edu.pe.core.domain.repositories.TipoUnidadRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Caso de Uso: Actualizar un tipo de unidad organizativa.
 */
@ApplicationScoped
public class ActualizarTipoUnidadUseCase {

    @Inject
    TipoUnidadRepository tipoUnidadRepository;

    @Transactional
    public TipoUnidad execute(ActualizarTipoUnidadCommand command) {
        TipoUnidad tipoUnidad = tipoUnidadRepository.findByIdOptional(command.id())
                .orElseThrow(() -> new NotFoundException("Tipo de unidad no encontrado con ID: " + command.id()));

        if (command.nombre() != null)
            tipoUnidad.setNombre(command.nombre());
        if (command.descripcion() != null)
            tipoUnidad.setDescripcion(command.descripcion());
        if (command.nivel() != null)
            tipoUnidad.setNivel(command.nivel());

        tipoUnidadRepository.persist(tipoUnidad);
        return tipoUnidad;
    }
}
