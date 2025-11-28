package upeu.edu.pe.academic.domain.repositories;

import upeu.edu.pe.academic.domain.entities.PeriodoAcademico;
import java.util.List;
import java.util.Optional;

public interface PeriodoAcademicoRepository {
    void persist(PeriodoAcademico periodoAcademico);

    Optional<PeriodoAcademico> findByIdOptional(Long id);

    Optional<PeriodoAcademico> findByCodigoPeriodo(String codigoPeriodo);

    Optional<PeriodoAcademico> findPeriodoActual(Long universidadId);

    List<PeriodoAcademico> findAllActive();

    List<PeriodoAcademico> findByUniversidad(Long universidadId);

    Optional<PeriodoAcademico> findByCodigoAndUniversidad(String codigo, Long universidadId);

    List<PeriodoAcademico> findByAnioAndUniversidad(Integer anio, Long universidadId);

    List<PeriodoAcademico> findByEstadoAndUniversidad(String estado, Long universidadId);

    List<PeriodoAcademico> findActivosAndUniversidad(Long universidadId);

    List<PeriodoAcademico> findByAnio(Integer anio);

    List<PeriodoAcademico> findByEstado(String estado);

    boolean existsByCodigoPeriodo(String codigoPeriodo);

    boolean existsByCodigoPeriodoAndIdNot(String codigoPeriodo, Long id);

    void desactivarOtrosPeriodosActuales(Long universidadId, Long periodoActualId);
}
