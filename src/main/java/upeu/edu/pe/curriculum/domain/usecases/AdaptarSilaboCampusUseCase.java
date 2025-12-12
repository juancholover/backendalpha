package upeu.edu.pe.curriculum.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.curriculum.domain.entities.SilaboPublicacion;
import upeu.edu.pe.curriculum.domain.repositories.SilaboPublicacionRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Use Case: Adaptar un sílabo publicado según las restricciones del campus.
 * 
 * Reglas:
 * 1. La publicación debe estar ACTIVA
 * 2. El campo a modificar debe estar permitido según nivelFlexibilidad
 * 3. Solo campus_admin puede ejecutar esto
 */
@ApplicationScoped
public class AdaptarSilaboCampusUseCase {

    @Inject
    SilaboPublicacionRepository publicacionRepository;

    @Transactional
    public SilaboPublicacion execute(
            Long publicacionId,
            String adaptadoPor,
            String notasAdaptacion) {

        // 1. Validar que la publicación existe
        SilaboPublicacion publicacion = publicacionRepository.findByIdOptional(publicacionId)
                .orElseThrow(() -> new NotFoundException("Publicación no encontrada con ID: " + publicacionId));

        // 2. Validar que está activa
        if (!"ACTIVA".equals(publicacion.getEstado())) {
            throw new BusinessException(
                "La publicación no está activa. Estado: " + publicacion.getEstado()
            );
        }

        // 3. Marcar como adaptado
        publicacion.marcarAdaptado(adaptadoPor, notasAdaptacion);

        publicacionRepository.persist(publicacion);

        return publicacion;
    }
}
