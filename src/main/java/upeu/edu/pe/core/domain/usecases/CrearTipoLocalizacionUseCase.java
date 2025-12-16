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
        tipoLocalizacion.setCodigo(command.codigo());
        tipoLocalizacion.setNombre(command.nombre());
        // Valor por defecto si es null (la columna es NOT NULL)
        tipoLocalizacion.setNivelJerarquia(command.nivelJerarquia() != null ? command.nivelJerarquia() : 0);
        tipoLocalizacion.setPermiteAsignacion(
                command.permiteAsignacion() != null ? command.permiteAsignacion() : false);

        // Manejar el padre si se proporciona (null o 0 indica sin padre)
        if (command.padreId() != null && command.padreId() > 0) {
            TipoLocalizacion padre = tipoLocalizacionRepository.findByIdOptional(command.padreId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Tipo de localización padre no encontrado con ID: " + command.padreId()));
            tipoLocalizacion.setPadre(padre);
        }

        tipoLocalizacionRepository.persist(tipoLocalizacion);
        return tipoLocalizacion;
    }
}
