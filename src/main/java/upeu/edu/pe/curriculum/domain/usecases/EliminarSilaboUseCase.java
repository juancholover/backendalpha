package upeu.edu.pe.curriculum.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.curriculum.domain.entities.Silabo;
import upeu.edu.pe.curriculum.domain.repositories.SilaboRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;
import upeu.edu.pe.shared.exceptions.BusinessException;

@ApplicationScoped
public class EliminarSilaboUseCase {

    @Inject
    SilaboRepository silaboRepository;

    @Transactional
    public void execute(Long id) {
        Silabo silabo = silaboRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Sílabo no encontrado con ID: " + id));

        if (!"BORRADOR".equals(silabo.getEstado())) {
            throw new BusinessException(
                    "Solo se pueden eliminar sílabos en borrador. Estado actual: " + silabo.getEstado());
        }

        silabo.setActive(false);
        silaboRepository.persist(silabo);
    }
}
