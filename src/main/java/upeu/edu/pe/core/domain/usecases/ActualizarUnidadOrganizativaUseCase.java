package upeu.edu.pe.core.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.core.domain.commands.ActualizarUnidadOrganizativaCommand;
import upeu.edu.pe.core.domain.entities.TipoUnidad;
import upeu.edu.pe.core.domain.entities.UnidadOrganizativa;
import upeu.edu.pe.core.domain.repositories.TipoUnidadRepository;
import upeu.edu.pe.core.domain.repositories.UnidadOrganizativaRepository;
import upeu.edu.pe.enrollment.domain.entities.Localizacion;
import upeu.edu.pe.enrollment.domain.repositories.LocalizacionRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Caso de Uso: Actualizar una unidad organizativa existente.
 * 
 * Reglas de negocio:
 * - La unidad debe existir
 * - El tipo de unidad debe existir
 * - El código debe ser único (excluyendo la unidad actual)
 * - El nombre debe ser único (excluyendo la unidad actual)
 * - Una unidad no puede ser padre de sí misma
 */
@ApplicationScoped
public class ActualizarUnidadOrganizativaUseCase {

    @Inject
    UnidadOrganizativaRepository unidadRepository;

    @Inject
    TipoUnidadRepository tipoUnidadRepository;

    @Inject
    LocalizacionRepository localizacionRepository;

    @Transactional
    public UnidadOrganizativa execute(ActualizarUnidadOrganizativaCommand command) {

        // 1. Buscar unidad existente
        UnidadOrganizativa unidad = unidadRepository.findByIdOptional(command.id())
                .orElseThrow(() -> new NotFoundException("Unidad organizativa no encontrada con ID: " + command.id()));

        // 2. Validar tipo de unidad
        TipoUnidad tipoUnidad = tipoUnidadRepository.findByIdOptional(command.tipoUnidadId())
                .orElseThrow(
                        () -> new NotFoundException("Tipo de unidad no encontrado con ID: " + command.tipoUnidadId()));

        // 3. Validar código único
        if (command.codigo() != null && unidadRepository.existsByCodigoAndIdNot(command.codigo(), command.id())) {
            throw new BusinessException("Ya existe otra unidad organizativa con el código: " + command.codigo());
        }

        // 4. Validar nombre único
        if (unidadRepository.existsByNombreAndIdNot(command.nombre(), command.id())) {
            throw new BusinessException("Ya existe otra unidad organizativa con el nombre: " + command.nombre());
        }

        // 5. Validar que no sea padre de sí misma
        if (command.unidadPadreId() != null && command.unidadPadreId().equals(command.id())) {
            throw new BusinessException("Una unidad no puede ser padre de sí misma");
        }

        // 6. Validar unidad padre
        UnidadOrganizativa unidadPadre = null;
        if (command.unidadPadreId() != null) {
            unidadPadre = unidadRepository.findByIdOptional(command.unidadPadreId())
                    .orElseThrow(() -> new NotFoundException(
                            "Unidad padre no encontrada con ID: " + command.unidadPadreId()));
        }

        // 7. Validar localización
        Localizacion localizacion = null;
        if (command.localizacionId() != null) {
            localizacion = localizacionRepository.findByIdOptional(command.localizacionId())
                    .orElseThrow(() -> new NotFoundException(
                            "Localización no encontrada con ID: " + command.localizacionId()));
        }

        // 8. Actualizar entidad
        unidad.setTipoUnidad(tipoUnidad);
        unidad.setCodigo(command.codigo());
        unidad.setNombre(command.nombre());
        unidad.setSigla(command.abreviatura());
        unidad.setDescripcion(command.descripcion());
        unidad.setUnidadPadre(unidadPadre);
        unidad.setLocalizacion(localizacion);

        // 9. Persistir
        unidadRepository.persist(unidad);

        return unidad;
    }
}
