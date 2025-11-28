package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.CursoOfertado;
import upeu.edu.pe.academic.domain.entities.Matricula;
import upeu.edu.pe.academic.domain.repositories.CursoOfertadoRepository;
import upeu.edu.pe.academic.domain.repositories.MatriculaRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class EliminarMatriculaUseCase {

    private final MatriculaRepository matriculaRepository;
    private final CursoOfertadoRepository cursoOfertadoRepository;

    @Inject
    public EliminarMatriculaUseCase(MatriculaRepository matriculaRepository,
            CursoOfertadoRepository cursoOfertadoRepository) {
        this.matriculaRepository = matriculaRepository;
        this.cursoOfertadoRepository = cursoOfertadoRepository;
    }

    public void execute(Long id) {
        Matricula matricula = matriculaRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matricula", "id", id));

        // Liberar cupo en el curso ofertado si está activa
        if ("MATRICULADO".equals(matricula.getEstadoMatricula())) {
            CursoOfertado cursoOfertado = matricula.getCursoOfertado();
            cursoOfertado.aumentarVacantes();
            cursoOfertadoRepository.persist(cursoOfertado);
        }

        // Soft delete
        matricula.setActive(false);
        matriculaRepository.persist(matricula);
    }
}
