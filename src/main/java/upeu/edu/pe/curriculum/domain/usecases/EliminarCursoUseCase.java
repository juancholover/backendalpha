package upeu.edu.pe.curriculum.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.curriculum.domain.entities.Curso;
import upeu.edu.pe.curriculum.domain.repositories.CursoRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Caso de Uso: Eliminar (soft delete) un curso.
 */
@ApplicationScoped
public class EliminarCursoUseCase {

    @Inject
    CursoRepository cursoRepository;

    @Transactional
    public void execute(Long cursoId) {
        Curso curso = cursoRepository.findByIdOptional(cursoId)
                .filter(c -> c.getActive())
                .orElseThrow(() -> new NotFoundException("Curso no encontrado con ID: " + cursoId));

        curso.setActive(false);
        cursoRepository.persist(curso);
    }
}
