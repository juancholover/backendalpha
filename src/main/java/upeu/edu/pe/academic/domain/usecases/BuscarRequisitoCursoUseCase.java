package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.RequisitoCurso;
import upeu.edu.pe.academic.domain.repositories.RequisitoCursoRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;

@ApplicationScoped
public class BuscarRequisitoCursoUseCase {

    private final RequisitoCursoRepository requisitoRepository;

    @Inject
    public BuscarRequisitoCursoUseCase(RequisitoCursoRepository requisitoRepository) {
        this.requisitoRepository = requisitoRepository;
    }

    public RequisitoCurso findById(Long id) {
        return requisitoRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("RequisitoCurso", "id", id));
    }

    public List<RequisitoCurso> findByCurso(Long cursoId) {
        return requisitoRepository.findByCurso(cursoId);
    }

    public List<RequisitoCurso> findPrerequisitosByCurso(Long cursoId) {
        return requisitoRepository.findPrerequisitosByCurso(cursoId);
    }

    public List<RequisitoCurso> findCorrequisitosByCurso(Long cursoId) {
        return requisitoRepository.findCorrequisitosByCurso(cursoId);
    }

    public List<RequisitoCurso> findCursosQueTienenComoRequisito(Long cursoRequisitoId) {
        return requisitoRepository.findCursosQueTienenComoRequisito(cursoRequisitoId);
    }

    public List<RequisitoCurso> findObligatoriosByCurso(Long cursoId) {
        return requisitoRepository.findObligatoriosByCurso(cursoId);
    }

    public List<RequisitoCurso> findAllRequisitosCascada(Long cursoId) {
        return requisitoRepository.findAllRequisitosCascada(cursoId);
    }

    public long countByCurso(Long cursoId) {
        return requisitoRepository.countByCurso(cursoId);
    }
}
