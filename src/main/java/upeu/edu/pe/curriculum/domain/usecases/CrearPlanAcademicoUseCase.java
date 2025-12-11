package upeu.edu.pe.curriculum.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.curriculum.domain.commands.CrearPlanAcademicoCommand;
import upeu.edu.pe.curriculum.domain.entities.PlanAcademico;
import upeu.edu.pe.curriculum.domain.entities.ProgramaAcademico;
import upeu.edu.pe.curriculum.domain.repositories.PlanAcademicoRepository;
import upeu.edu.pe.curriculum.domain.repositories.ProgramaAcademicoRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

@ApplicationScoped
public class CrearPlanAcademicoUseCase {

    @Inject
    PlanAcademicoRepository planRepository;

    @Inject
    ProgramaAcademicoRepository programaRepository;

    @Transactional
    public PlanAcademico execute(CrearPlanAcademicoCommand command) {
        // 1. Validar código único
        if (planRepository.existsByCodigo(command.codigo())) {
            throw new BusinessException("Ya existe un plan académico con el código: " + command.codigo());
        }

        // 2. Obtener programa académico
        ProgramaAcademico programa = programaRepository.findByIdOptional(command.programaAcademicoId())
                .orElseThrow(() -> new NotFoundException(
                        "Programa académico no encontrado con ID: " + command.programaAcademicoId()));

        // 3. Validar fechas
        if (command.fechaVigenciaFin() != null && command.fechaVigenciaInicio() != null &&
                command.fechaVigenciaFin().isBefore(command.fechaVigenciaInicio())) {
            throw new BusinessException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        // 4. Crear plan
        PlanAcademico plan = new PlanAcademico();
        plan.setProgramaAcademico(programa);
        plan.setCodigo(command.codigo());
        plan.setNombre(command.nombre());
        plan.setFechaVigenciaInicio(command.fechaVigenciaInicio());
        plan.setFechaVigenciaFin(command.fechaVigenciaFin());
        plan.setCreditosTotales(command.creditosTotales());
        plan.setDuracionSemestres(command.duracionSemestres());
        plan.setEstado(command.estado() != null ? command.estado() : "VIGENTE");

        planRepository.persist(plan);
        return plan;
    }
}
