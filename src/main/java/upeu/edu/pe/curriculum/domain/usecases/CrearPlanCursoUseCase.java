package upeu.edu.pe.curriculum.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.curriculum.domain.commands.CrearPlanCursoCommand;
import upeu.edu.pe.curriculum.domain.entities.Curso;
import upeu.edu.pe.curriculum.domain.entities.PlanAcademico;
import upeu.edu.pe.curriculum.domain.entities.PlanCurso;
import upeu.edu.pe.curriculum.domain.repositories.CursoRepository;
import upeu.edu.pe.curriculum.domain.repositories.PlanAcademicoRepository;
import upeu.edu.pe.curriculum.domain.repositories.PlanCursoRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

@ApplicationScoped
public class CrearPlanCursoUseCase {

    @Inject
    PlanCursoRepository planCursoRepository;

    @Inject
    PlanAcademicoRepository planAcademicoRepository;

    @Inject
    CursoRepository cursoRepository;

    @Transactional
    public PlanCurso execute(CrearPlanCursoCommand command) {
        // 1. Validar duplicado
        if (planCursoRepository.existsByPlanAcademicoAndCurso(command.planAcademicoId(), command.cursoId())) {
            throw new BusinessException("El curso ya existe en este plan académico");
        }

        // 2. Obtener plan académico
        PlanAcademico planAcademico = planAcademicoRepository.findByIdOptional(command.planAcademicoId())
                .filter(PlanAcademico::getActive)
                .orElseThrow(() -> new NotFoundException(
                        "Plan académico no encontrado con ID: " + command.planAcademicoId()));

        // 3. Obtener curso
        Curso curso = cursoRepository.findByIdOptional(command.cursoId())
                .filter(Curso::getActive)
                .orElseThrow(() -> new NotFoundException("Curso no encontrado con ID: " + command.cursoId()));

        // 4. Crear plan-curso
        PlanCurso planCurso = new PlanCurso();
        planCurso.setPlanAcademico(planAcademico);
        planCurso.setCurso(curso);
        planCurso.setCreditos(command.creditos());
        planCurso.setCiclo(command.ciclo());
        planCurso.setTipoCurso(command.tipoCurso());
        planCurso.setEsObligatorio(command.esObligatorio());

        planCursoRepository.persist(planCurso);
        return planCurso;
    }
}
