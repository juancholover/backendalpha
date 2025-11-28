package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.Curso;
import upeu.edu.pe.academic.domain.repositories.CursoRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class EliminarCursoUseCase {

    private final CursoRepository cursoRepository;

    @Inject
    public EliminarCursoUseCase(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    public void execute(Long id) {
        Curso curso = cursoRepository.findByIdOptional(id)
                .filter(c -> c.getActive())
                .orElseThrow(() -> new ResourceNotFoundException("Curso", "id", id));

        // NOTA: prerequisitos están en RequisitoCurso (consultar ahí antes de eliminar)
        // Por ahora mantenemos la lógica simple del servicio original

        curso.setActive(false);
        cursoRepository.persist(curso);
    }
}
