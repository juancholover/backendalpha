package upeu.edu.pe.people.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.people.domain.entities.Profesor;
import upeu.edu.pe.people.domain.repositories.ProfesorRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Caso de Uso: Eliminar (soft delete) un profesor.
 */
@ApplicationScoped
public class EliminarProfesorUseCase {

    @Inject
    ProfesorRepository profesorRepository;

    @Transactional
    public void execute(Long profesorId) {
        Profesor profesor = profesorRepository.findByIdOptional(profesorId)
                .orElseThrow(() -> new NotFoundException("Profesor no encontrado con ID: " + profesorId));

        profesor.setActive(false);
        profesorRepository.persist(profesor);
    }
}
