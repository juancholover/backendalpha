package upeu.edu.pe.people.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.people.domain.entities.Autoridad;
import upeu.edu.pe.people.domain.repositories.AutoridadRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

@ApplicationScoped
public class EliminarAutoridadUseCase {

    @Inject
    AutoridadRepository autoridadRepository;

    @Transactional
    public void execute(Long id) {
        Autoridad autoridad = autoridadRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Autoridad no encontrada con ID: " + id));

        autoridad.setActive(false);
        autoridadRepository.persist(autoridad);
    }
}
