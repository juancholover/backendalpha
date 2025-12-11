package upeu.edu.pe.enrollment.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.enrollment.domain.entities.Localizacion;
import upeu.edu.pe.enrollment.domain.repositories.LocalizacionRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

@ApplicationScoped
public class EliminarLocalizacionUseCase {

    @Inject
    LocalizacionRepository localizacionRepository;

    @Transactional
    public void execute(Long id) {
        Localizacion localizacion = localizacionRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Localización no encontrada con ID: " + id));

        localizacionRepository.delete(localizacion);
    }
}
