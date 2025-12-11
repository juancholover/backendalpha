package upeu.edu.pe.curriculum.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.curriculum.domain.entities.PlanAcademico;
import upeu.edu.pe.curriculum.domain.repositories.PlanAcademicoRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

@ApplicationScoped
public class EliminarPlanAcademicoUseCase {

    @Inject
    PlanAcademicoRepository planRepository;

    @Transactional
    public void execute(Long id) {
        PlanAcademico plan = planRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Plan académico no encontrado con ID: " + id));

        plan.setActive(false);
        planRepository.persist(plan);
    }
}
