package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.Curso;
import upeu.edu.pe.academic.domain.repositories.CursoRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;

@ApplicationScoped
public class BuscarCursoUseCase {

    private final CursoRepository cursoRepository;

    @Inject
    public BuscarCursoUseCase(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    public Curso findById(Long id) {
        return cursoRepository.findByIdOptional(id)
                .filter(c -> c.getActive())
                .orElseThrow(() -> new ResourceNotFoundException("Curso", "id", id));
    }

    public Curso findByCodigoCurso(String codigoCurso) {
        return cursoRepository.findByCodigoCurso(codigoCurso)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", "codigoCurso", codigoCurso));
    }

    public List<Curso> findAllActive() {
        return cursoRepository.findAllActive();
    }

    public List<Curso> findByUniversidad(Long universidadId) {
        return cursoRepository.findByUniversidad(universidadId);
    }
}
