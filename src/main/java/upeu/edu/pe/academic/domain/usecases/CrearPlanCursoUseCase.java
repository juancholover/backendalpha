package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.Curso;
import upeu.edu.pe.academic.domain.entities.PlanAcademico;
import upeu.edu.pe.academic.domain.entities.PlanCurso;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.academic.domain.repositories.CursoRepository;
import upeu.edu.pe.academic.domain.repositories.PlanAcademicoRepository;
import upeu.edu.pe.academic.domain.repositories.PlanCursoRepository;
import upeu.edu.pe.academic.domain.repositories.UniversidadRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class CrearPlanCursoUseCase {

    private final PlanCursoRepository planCursoRepository;
    private final UniversidadRepository universidadRepository;
    private final PlanAcademicoRepository planAcademicoRepository;
    private final CursoRepository cursoRepository;

    @Inject
    public CrearPlanCursoUseCase(PlanCursoRepository planCursoRepository,
            UniversidadRepository universidadRepository,
            PlanAcademicoRepository planAcademicoRepository,
            CursoRepository cursoRepository) {
        this.planCursoRepository = planCursoRepository;
        this.universidadRepository = universidadRepository;
        this.planAcademicoRepository = planAcademicoRepository;
        this.cursoRepository = cursoRepository;
    }

    public PlanCurso execute(Long universidadId, Long planAcademicoId, Long cursoId,
            Integer creditos, Integer ciclo, String tipoCurso, Boolean esObligatorio) {

        // Validar que el curso no exista ya en el plan
        if (planCursoRepository.existsByPlanAcademicoAndCurso(planAcademicoId, cursoId)) {
            throw new BusinessException("El curso ya existe en este plan académico");
        }

        Universidad universidad = universidadRepository.findByIdOptional(universidadId)
                .orElseThrow(() -> new ResourceNotFoundException("Universidad", "id", universidadId));

        PlanAcademico planAcademico = planAcademicoRepository.findByIdOptional(planAcademicoId)
                .filter(PlanAcademico::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("PlanAcademico", "id", planAcademicoId));

        Curso curso = cursoRepository.findByIdOptional(cursoId)
                .filter(Curso::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", "id", cursoId));

        PlanCurso planCurso = PlanCurso.crear(universidad, planAcademico, curso, creditos, ciclo, tipoCurso,
                esObligatorio);

        planCursoRepository.persist(planCurso);
        return planCurso;
    }
}
