package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.Matricula;
import upeu.edu.pe.academic.domain.repositories.MatriculaRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;

@ApplicationScoped
public class BuscarMatriculaUseCase {

    private final MatriculaRepository repository;

    @Inject
    public BuscarMatriculaUseCase(MatriculaRepository repository) {
        this.repository = repository;
    }

    public Matricula findById(Long id) {
        return repository.findByIdOptional(id)
                .filter(Matricula::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Matricula", "id", id));
    }

    public List<Matricula> findByEstudiante(Long estudianteId) {
        return repository.findByEstudiante(estudianteId);
    }

    public List<Matricula> findByCursoOfertado(Long cursoOfertadoId) {
        return repository.findByCursoOfertado(cursoOfertadoId);
    }

    public List<Matricula> findByPeriodo(Long periodoId) {
        return repository.findByPeriodo(periodoId);
    }

    public List<Matricula> findByEstudianteAndPeriodo(Long estudianteId, Long periodoId) {
        return repository.findByEstudianteAndPeriodo(estudianteId, periodoId);
    }

    public List<Matricula> findByEstadoMatricula(String estadoMatricula) {
        return repository.findByEstadoMatricula(estadoMatricula);
    }

    public List<Matricula> findByPeriodoAndActive(Long periodoId) {
        return repository.findByPeriodoAndActive(periodoId);
    }

    public List<Matricula> findByEstudianteAndAprobado(Long estudianteId) {
        return repository.findByEstudianteAndAprobado(estudianteId);
    }

    public List<Matricula> findByTipoAndPeriodo(String tipoMatricula, Long periodoId) {
        return repository.findByTipoAndPeriodo(tipoMatricula, periodoId);
    }

    public List<Matricula> findAllActive() {
        return repository.findAllActive();
    }

    public Matricula findByEstudianteAndCurso(Long estudianteId, Long cursoOfertadoId) {
        return repository.findByEstudianteAndCurso(estudianteId, cursoOfertadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Matricula", "estudianteId + cursoOfertadoId",
                        estudianteId + " + " + cursoOfertadoId));
    }
}
