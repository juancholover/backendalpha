package upeu.edu.pe.curriculum.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.curriculum.domain.entities.PlanCurso;
import upeu.edu.pe.curriculum.domain.repositories.PlanCursoRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

@ApplicationScoped
public class EliminarPlanCursoUseCase {

    @Inject
    PlanCursoRepository planCursoRepository;

    @Transactional
    public void execute(Long id) {
        PlanCurso planCurso = planCursoRepository.findByIdOptional(id)
                .filter(PlanCurso::getActive)
                .orElseThrow(() -> new NotFoundException("PlanCurso no encontrado con ID: " + id));

        planCurso.setActive(false);
        planCursoRepository.persist(planCurso);
    }
}
