package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.PlanCurso;
import upeu.edu.pe.academic.domain.repositories.PlanCursoRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class EliminarPlanCursoUseCase {

    private final PlanCursoRepository planCursoRepository;

    @Inject
    public EliminarPlanCursoUseCase(PlanCursoRepository planCursoRepository) {
        this.planCursoRepository = planCursoRepository;
    }

    public void execute(Long id) {
        PlanCurso planCurso = planCursoRepository.findByIdOptional(id)
                .filter(PlanCurso::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("PlanCurso", "id", id));

        planCurso.setActive(false);
        planCursoRepository.persist(planCurso);
    }
}
