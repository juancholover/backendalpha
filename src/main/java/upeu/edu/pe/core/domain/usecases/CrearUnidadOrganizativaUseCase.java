package upeu.edu.pe.core.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.core.domain.commands.CrearUnidadOrganizativaCommand;
import upeu.edu.pe.core.domain.entities.TipoUnidad;
import upeu.edu.pe.core.domain.entities.UnidadOrganizativa;
import upeu.edu.pe.core.domain.repositories.TipoUnidadRepository;
import upeu.edu.pe.core.domain.repositories.UnidadOrganizativaRepository;
import upeu.edu.pe.enrollment.domain.entities.Localizacion;
import upeu.edu.pe.enrollment.domain.repositories.LocalizacionRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Caso de Uso: Crear una nueva unidad organizativa.
 * 
 * Reglas de negocio:
 * - El tipo de unidad debe existir
 * - El código debe ser único
 * - El nombre debe ser único
 * - La unidad padre debe existir (si se especifica)
 * - La localización debe existir (si se especifica)
 */
@ApplicationScoped
public class CrearUnidadOrganizativaUseCase {

    @Inject
    UnidadOrganizativaRepository unidadRepository;

    @Inject
    TipoUnidadRepository tipoUnidadRepository;

    @Inject
    LocalizacionRepository localizacionRepository;

    @Transactional
    public UnidadOrganizativa execute(CrearUnidadOrganizativaCommand command) {

        // 1. Validar tipo de unidad
        TipoUnidad tipoUnidad = tipoUnidadRepository.findByIdOptional(command.tipoUnidadId())
                .orElseThrow(
                        () -> new NotFoundException("Tipo de unidad no encontrado con ID: " + command.tipoUnidadId()));

        // 2. Validar código único
        if (command.codigo() != null && unidadRepository.existsByCodigo(command.codigo())) {
            throw new BusinessException("Ya existe una unidad organizativa con el código: " + command.codigo());
        }

        // 3. Validar nombre único
        if (unidadRepository.existsByNombre(command.nombre())) {
            throw new BusinessException("Ya existe una unidad organizativa con el nombre: " + command.nombre());
        }

        // 4. Validar unidad padre (si se especifica)
        UnidadOrganizativa unidadPadre = null;
        if (command.unidadPadreId() != null) {
            unidadPadre = unidadRepository.findByIdOptional(command.unidadPadreId())
                    .orElseThrow(() -> new NotFoundException(
                            "Unidad padre no encontrada con ID: " + command.unidadPadreId()));
        }

        // 5. Validar localización (si se especifica)
        Localizacion localizacion = null;
        if (command.localizacionId() != null) {
            localizacion = localizacionRepository.findByIdOptional(command.localizacionId())
                    .orElseThrow(() -> new NotFoundException(
                            "Localización no encontrada con ID: " + command.localizacionId()));
        }

        // 6. Crear entidad
        UnidadOrganizativa unidad = new UnidadOrganizativa();
        unidad.setTipoUnidad(tipoUnidad);
        unidad.setCodigo(command.codigo());
        unidad.setNombre(command.nombre());
        unidad.setSigla(command.abreviatura());
        unidad.setDescripcion(command.descripcion());
        unidad.setUnidadPadre(unidadPadre);
        unidad.setLocalizacion(localizacion);

        // 7. Persistir
        unidadRepository.persist(unidad);

        return unidad;
    }
}
