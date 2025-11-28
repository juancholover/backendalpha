package upeu.edu.pe.academic.domain.repositories;

import upeu.edu.pe.academic.domain.entities.Matricula;
import java.util.List;
import java.util.Optional;

public interface MatriculaRepository {
    void persist(Matricula matricula);

    Optional<Matricula> findByIdOptional(Long id);

    List<Matricula> findByEstudiante(Long estudianteId);

    List<Matricula> findByCursoOfertado(Long cursoOfertadoId);

    List<Matricula> findByPeriodo(Long periodoId);

    Optional<Matricula> findByEstudianteAndCurso(Long estudianteId, Long cursoOfertadoId);

    boolean existsByEstudianteAndCurso(Long estudianteId, Long cursoOfertadoId);

    List<Matricula> findByEstudianteAndPeriodo(Long estudianteId, Long periodoId);

    List<Matricula> findByEstadoMatricula(String estadoMatricula);

    List<Matricula> findByPeriodoAndActive(Long periodoId);

    List<Matricula> findByEstudianteAndAprobado(Long estudianteId);

    List<Matricula> findByTipoAndPeriodo(String tipoMatricula, Long periodoId);

    List<Matricula> findAllActive();
}
