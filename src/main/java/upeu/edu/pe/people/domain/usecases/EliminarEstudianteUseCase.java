package upeu.edu.pe.people.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.people.domain.entities.Estudiante;
import upeu.edu.pe.people.domain.repositories.EstudianteRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

@ApplicationScoped
public class EliminarEstudianteUseCase {

    @Inject
    EstudianteRepository estudianteRepository;

    @Transactional
    public void execute(Long id) {
        Estudiante estudiante = estudianteRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Estudiante no encontrado con ID: " + id));

        estudiante.setActive(false);
        estudianteRepository.persist(estudiante);
    }
}
