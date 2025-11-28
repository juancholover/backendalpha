package upeu.edu.pe.academic.domain.repositories;

import upeu.edu.pe.academic.domain.entities.CursoOfertado;
import java.util.List;
import java.util.Optional;

public interface CursoOfertadoRepository {
    void persist(CursoOfertado cursoOfertado);

    Optional<CursoOfertado> findByIdOptional(Long id);

    List<CursoOfertado> findByUniversidad(Long universidadId);

    List<CursoOfertado> findByPeriodoAcademico(Long periodoId);

    List<CursoOfertado> findByPlanAcademico(Long planId);

    List<CursoOfertado> findByPlanCurso(Long planCursoId);

    List<CursoOfertado> findByCurso(Long cursoId);

    List<CursoOfertado> findByProfesor(Long profesorId);

    List<CursoOfertado> findAbiertasByPeriodoAndUniversidad(Long periodoId, Long universidadId);

    List<CursoOfertado> findByModalidadAndPeriodo(String modalidad, Long periodoId);

    List<CursoOfertado> findConVacantesByPeriodo(Long periodoId);

    Optional<CursoOfertado> findByCodigoAndPeriodoAndUniversidad(String codigoSeccion, Long periodoId,
            Long universidadId);

    boolean existsByCodigoAndPeriodoAndPlanCurso(String codigoSeccion, Long periodoId, Long planCursoId);

    long countMatriculados(Long cursoOfertadoId);
}
