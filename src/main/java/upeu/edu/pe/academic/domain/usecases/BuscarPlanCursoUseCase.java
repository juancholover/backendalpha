package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.PlanCurso;
import upeu.edu.pe.academic.domain.repositories.PlanCursoRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;

@ApplicationScoped
public class BuscarPlanCursoUseCase {

    private final PlanCursoRepository planCursoRepository;

    @Inject
    public BuscarPlanCursoUseCase(PlanCursoRepository planCursoRepository) {
        this.planCursoRepository = planCursoRepository;
    }

    public PlanCurso findById(Long id) {
        return planCursoRepository.findByIdOptional(id)
                .filter(PlanCurso::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("PlanCurso", "id", id));
    }

    public List<PlanCurso> findByPlanAcademico(Long planAcademicoId) {
        return planCursoRepository.findByPlanAcademico(planAcademicoId);
    }

    public List<PlanCurso> findByPlanAcademicoAndCiclo(Long planAcademicoId, Integer ciclo) {
        return planCursoRepository.findByPlanAcademicoAndCiclo(planAcademicoId, ciclo);
    }

    public List<PlanCurso> findByCurso(Long cursoId) {
        return planCursoRepository.findByCurso(cursoId);
    }

    public List<PlanCurso> findObligatoriosByPlanAcademico(Long planAcademicoId) {
        return planCursoRepository.findObligatoriosByPlanAcademico(planAcademicoId);
    }

    public List<PlanCurso> findElectivosByPlanAcademico(Long planAcademicoId) {
        return planCursoRepository.findElectivosByPlanAcademico(planAcademicoId);
    }

    public Integer calcularCreditosTotales(Long planAcademicoId) {
        Integer obligatorios = planCursoRepository.sumCreditosObligatorios(planAcademicoId);
        Integer electivos = planCursoRepository.sumCreditosElectivos(planAcademicoId);
        return (obligatorios != null ? obligatorios : 0) + (electivos != null ? electivos : 0);
    }
}
