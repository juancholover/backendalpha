package upeu.edu.pe.enrollment.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.curriculum.domain.entities.PlanAcademico;
import upeu.edu.pe.enrollment.domain.commands.MatricularEstudianteCommand;
import upeu.edu.pe.enrollment.domain.entities.CursoOfertado;
import upeu.edu.pe.enrollment.domain.entities.Matricula;
import upeu.edu.pe.enrollment.domain.repositories.CursoOfertadoRepository;
import upeu.edu.pe.enrollment.domain.repositories.MatriculaRepository;
import upeu.edu.pe.people.domain.entities.Estudiante;
import upeu.edu.pe.people.domain.repositories.EstudianteRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.DuplicateResourceException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.time.LocalDate;

/**
 * Caso de Uso: Matricular un estudiante en un curso ofertado.
 * 
 * Reglas de negocio:
 * - Estudiante debe existir
 * - Curso ofertado debe existir
 * - No puede haber matrícula duplicada
 * - Debe haber cupo disponible
 * - No debe exceder límite de créditos por ciclo
 */
@ApplicationScoped
public class MatricularEstudianteUseCase {

    @Inject
    MatriculaRepository matriculaRepository;

    @Inject
    EstudianteRepository estudianteRepository;

    @Inject
    CursoOfertadoRepository cursoOfertadoRepository;

    @Transactional
    public Matricula execute(MatricularEstudianteCommand command) {

        // 1. Validar que no exista matrícula duplicada
        if (matriculaRepository.existsByEstudianteAndSeccion(command.estudianteId(), command.seccionId())) {
            throw new DuplicateResourceException("El estudiante ya está matriculado en esta sección");
        }

        // 2. Obtener estudiante
        Estudiante estudiante = estudianteRepository.findByIdOptional(command.estudianteId())
                .orElseThrow(() -> new NotFoundException("Estudiante no encontrado con ID: " + command.estudianteId()));

        // 3. Obtener curso ofertado
        CursoOfertado cursoOfertado = cursoOfertadoRepository.findByIdOptional(command.seccionId())
                .orElseThrow(
                        () -> new NotFoundException("Curso ofertado no encontrado con ID: " + command.seccionId()));

        // 4. Verificar cupo disponible
        if (!cursoOfertado.hayCupoDisponible()) {
            throw new BusinessException("El curso ofertado no tiene cupo disponible");
        }

        // 5. Validar límite de créditos por ciclo
        Integer creditosCurso = cursoOfertado.getPlanCurso().getCreditos();
        PlanAcademico planAcademico = cursoOfertado.getPlanCurso().getPlanAcademico();
        Integer creditosActuales = estudiante.getCreditosCursando() != null ? estudiante.getCreditosCursando() : 0;
        Integer nuevoTotalCreditos = creditosActuales + creditosCurso;

        if (planAcademico.getCreditosMaximosPorCiclo() != null &&
                nuevoTotalCreditos > planAcademico.getCreditosMaximosPorCiclo()) {
            throw new BusinessException(
                    "El estudiante excedería el límite de créditos por ciclo. " +
                            "Actual: " + creditosActuales + ", Curso: " + creditosCurso +
                            ", Máximo: " + planAcademico.getCreditosMaximosPorCiclo());
        }

        // 6. Crear matrícula
        Matricula matricula = new Matricula();
        matricula.setEstudiante(estudiante);
        matricula.setCursoOfertado(cursoOfertado);
        matricula.setFechaMatricula(LocalDate.now());
        matricula.setEstadoMatricula("MATRICULADO");
        matricula.setTipoMatricula(command.tipoMatricula() != null ? command.tipoMatricula() : "REGULAR");
        matricula.setCreditosMatriculados(creditosCurso);

        // 7. Actualizar créditos del estudiante
        estudiante.setCreditosCursando(nuevoTotalCreditos);
        estudianteRepository.persist(estudiante);

        // 8. Reducir vacantes del curso
        cursoOfertado.reducirVacantes();
        cursoOfertadoRepository.persist(cursoOfertado);

        // 9. Persistir matrícula
        matriculaRepository.persist(matricula);

        return matricula;
    }
}
