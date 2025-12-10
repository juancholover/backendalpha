package upeu.edu.pe.core.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.core.domain.entities.UnidadOrganizativa;
import upeu.edu.pe.core.domain.repositories.UnidadOrganizativaRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Caso de Uso: Eliminar (soft delete) una unidad organizativa.
 * 
 * Reglas de negocio:
 * - La unidad debe existir
 * - No puede tener unidades hijas
 */
@ApplicationScoped
public class EliminarUnidadOrganizativaUseCase {

    @Inject
    UnidadOrganizativaRepository unidadRepository;

    @Transactional
    public void execute(Long unidadId) {

        // 1. Buscar unidad
        UnidadOrganizativa unidad = unidadRepository.findByIdOptional(unidadId)
                .orElseThrow(() -> new NotFoundException("Unidad organizativa no encontrada con ID: " + unidadId));

        // 2. Validar que no tenga hijas
        if (unidadRepository.hasUnidadesHijas(unidadId)) {
            throw new BusinessException("No se puede eliminar la unidad porque tiene unidades hijas asociadas");
        }

        // 3. Soft delete
        unidad.setActive(false);
        unidadRepository.persist(unidad);
    }
}
