package upeu.edu.pe.curriculum.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.curriculum.domain.entities.SilaboUnidad;
import upeu.edu.pe.curriculum.domain.repositories.SilaboUnidadRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;
import upeu.edu.pe.shared.exceptions.BusinessException;

@ApplicationScoped
public class EliminarUnidadSilaboUseCase {

    @Inject
    SilaboUnidadRepository silaboUnidadRepository;

    @Transactional
    public void execute(Long id) {
        SilaboUnidad unidad = silaboUnidadRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Unidad no encontrada con ID: " + id));

        if (!unidad.getSilabo().esModificable()) {
            throw new BusinessException(
                    "No se puede eliminar una unidad de un sílabo en estado " + unidad.getSilabo().getEstado());
        }

        unidad.setActive(false);
        silaboUnidadRepository.persist(unidad);
    }
}
