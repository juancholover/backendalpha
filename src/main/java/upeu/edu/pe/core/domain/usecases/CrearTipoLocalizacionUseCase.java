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
        tipoLocalizacion.setNivelJerarquia(command.nivelJerarquia());
        tipoLocalizacion.setPermiteAsignacion(
                command.permiteAsignacion() != null ? command.permiteAsignacion() : false);

        // Manejar el padre si se proporciona
        if (command.padreId() != null) {
            TipoLocalizacion padre = tipoLocalizacionRepository.findByIdOptional(command.padreId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Tipo de localización padre no encontrado con ID: " + command.padreId()));
            tipoLocalizacion.setPadre(padre);
        }

        tipoLocalizacionRepository.persist(tipoLocalizacion);
        return tipoLocalizacion;
    }
}
