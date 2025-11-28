package upeu.edu.pe.academic.domain.repositories;

import upeu.edu.pe.academic.domain.entities.RequisitoCurso;
import java.util.List;
import java.util.Optional;

public interface RequisitoCursoRepository {
    void persist(RequisitoCurso requisitoCurso);

    Optional<RequisitoCurso> findByIdOptional(Long id);

    List<RequisitoCurso> findByCurso(Long cursoId);

    List<RequisitoCurso> findPrerequisitosByCurso(Long cursoId);

    List<RequisitoCurso> findCorrequisitosByCurso(Long cursoId);

    List<RequisitoCurso> findCursosQueTienenComoRequisito(Long cursoRequisitoId);

    boolean existsRequisito(Long cursoId, Long cursoRequisitoId);

    Optional<RequisitoCurso> findByCursoAndRequisito(Long cursoId, Long cursoRequisitoId, String tipoRequisito);

    List<RequisitoCurso> findObligatoriosByCurso(Long cursoId);

    long countByCurso(Long cursoId);

    List<RequisitoCurso> findAllRequisitosCascada(Long cursoId);
}
