package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.PlanCurso;
import upeu.edu.pe.academic.domain.repositories.PlanCursoRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class ActualizarPlanCursoUseCase {

    private final PlanCursoRepository planCursoRepository;

    @Inject
    public ActualizarPlanCursoUseCase(PlanCursoRepository planCursoRepository) {
        this.planCursoRepository = planCursoRepository;
    }

    public PlanCurso execute(Long id, Integer creditos, Integer ciclo, String tipoCurso, Boolean esObligatorio) {
        PlanCurso planCurso = planCursoRepository.findByIdOptional(id)
                .filter(PlanCurso::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("PlanCurso", "id", id));

        // Actualizar solo los campos modificables (NO cambiar plan ni curso)
        planCurso.setCreditos(creditos);
        planCurso.setCiclo(ciclo);

        if (esObligatorio != null) {
            planCurso.setEsObligatorio(esObligatorio);
            planCurso.setTipoCurso(tipoCurso != null ? tipoCurso : (esObligatorio ? "OBLIGATORIO" : "ELECTIVO"));
        } else {
            if (tipoCurso != null) {
                planCurso.setTipoCurso(tipoCurso);
                planCurso.setEsObligatorio("OBLIGATORIO".equals(tipoCurso));
            }
        }

        planCursoRepository.persist(planCurso);
        return planCurso;
    }
}
