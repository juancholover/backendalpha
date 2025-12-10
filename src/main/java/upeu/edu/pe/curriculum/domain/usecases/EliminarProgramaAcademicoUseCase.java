package upeu.edu.pe.curriculum.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.curriculum.domain.entities.ProgramaAcademico;
import upeu.edu.pe.curriculum.domain.repositories.ProgramaAcademicoRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Caso de Uso: Eliminar (soft delete) un programa académico.
 */
@ApplicationScoped
public class EliminarProgramaAcademicoUseCase {

    @Inject
    ProgramaAcademicoRepository programaRepository;

    @Transactional
    public void execute(Long programaId) {
        ProgramaAcademico programa = programaRepository.findByIdOptional(programaId)
                .orElseThrow(() -> new NotFoundException("Programa académico no encontrado con ID: " + programaId));

        programa.setActive(false);
        programa.setEstado("INACTIVO");
        programaRepository.persist(programa);
    }
}
