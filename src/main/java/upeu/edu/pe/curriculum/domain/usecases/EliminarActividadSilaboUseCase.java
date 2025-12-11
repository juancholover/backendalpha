package upeu.edu.pe.curriculum.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.curriculum.domain.entities.SilaboActividad;
import upeu.edu.pe.curriculum.domain.repositories.SilaboActividadRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;
import upeu.edu.pe.shared.exceptions.BusinessException;

@ApplicationScoped
public class EliminarActividadSilaboUseCase {

    @Inject
    SilaboActividadRepository actividadRepository;

    @Transactional
    public void execute(Long id) {
        SilaboActividad actividad = actividadRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Actividad no encontrada con ID: " + id));

        if (!actividad.getUnidad().getSilabo().esModificable()) {
            throw new BusinessException(
                    "No se puede eliminar una actividad de un sílabo en estado "
                            + actividad.getUnidad().getSilabo().getEstado());
        }

        actividad.setActive(false);
        actividadRepository.persist(actividad);
    }
}
