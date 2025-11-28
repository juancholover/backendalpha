package upeu.edu.pe.academic.domain.repositories;

import upeu.edu.pe.academic.domain.entities.PlanCurso;
import java.util.List;
import java.util.Optional;

public interface PlanCursoRepository {
    void persist(PlanCurso planCurso);

    Optional<PlanCurso> findByIdOptional(Long id);

    List<PlanCurso> findByPlanAcademico(Long planAcademicoId);

    List<PlanCurso> findByPlanAcademicoAndCiclo(Long planAcademicoId, Integer ciclo);

    List<PlanCurso> findByCurso(Long cursoId);

    List<PlanCurso> findObligatoriosByPlanAcademico(Long planAcademicoId);

    List<PlanCurso> findElectivosByPlanAcademico(Long planAcademicoId);

    boolean existsByPlanAcademicoAndCurso(Long planAcademicoId, Long cursoId);

    Optional<PlanCurso> findByPlanAcademicoAndCurso(Long planAcademicoId, Long cursoId);

    Integer sumCreditosObligatorios(Long planAcademicoId);

    Integer sumCreditosElectivos(Long planAcademicoId);
}
