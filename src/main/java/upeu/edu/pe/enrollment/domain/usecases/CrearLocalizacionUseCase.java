package upeu.edu.pe.enrollment.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.enrollment.domain.commands.CrearLocalizacionCommand;
import upeu.edu.pe.enrollment.domain.entities.Localizacion;
import upeu.edu.pe.enrollment.domain.repositories.LocalizacionRepository;
import upeu.edu.pe.core.domain.entities.TipoLocalizacion;
import upeu.edu.pe.core.domain.repositories.TipoLocalizacionRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

@ApplicationScoped
public class CrearLocalizacionUseCase {

    @Inject
    LocalizacionRepository localizacionRepository;

    @Inject
    TipoLocalizacionRepository tipoLocalizacionRepository;

    @Transactional
    public Localizacion execute(CrearLocalizacionCommand command) {
        // 1. Validar tipo de localización
        TipoLocalizacion tipo = tipoLocalizacionRepository.findByIdOptional(command.tipoLocalizacionId())
                .orElseThrow(() -> new NotFoundException("Tipo de localización no encontrado"));

        // 2. Crear localización
        Localizacion localizacion = new Localizacion();
        localizacion.setTipoLocalizacion(tipo);
        localizacion.setCodigo(command.codigo());
        localizacion.setNombre(command.nombre());
        localizacion.setDireccion(command.direccion());
        localizacion.setTelefono(command.telefono());
        localizacion.setEmail(command.email());
        localizacion.setEsPrincipal(command.esPrincipal() != null ? command.esPrincipal() : false);

        localizacionRepository.persist(localizacion);
        return localizacion;
    }
}
