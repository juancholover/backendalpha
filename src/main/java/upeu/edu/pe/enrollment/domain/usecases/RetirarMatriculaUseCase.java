package upeu.edu.pe.enrollment.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.enrollment.domain.entities.CursoOfertado;
import upeu.edu.pe.enrollment.domain.entities.Matricula;
import upeu.edu.pe.enrollment.domain.repositories.CursoOfertadoRepository;
import upeu.edu.pe.enrollment.domain.repositories.MatriculaRepository;
import upeu.edu.pe.people.domain.entities.Estudiante;
import upeu.edu.pe.people.domain.repositories.EstudianteRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Caso de Uso: Retirar un estudiante de un curso.
 * 
 * Reglas de negocio:
 * - La matrícula debe existir
 * - Liberar cupo del curso
 * - Restar créditos del estudiante
 */
@ApplicationScoped
public class RetirarMatriculaUseCase {

    @Inject
    MatriculaRepository matriculaRepository;

    @Inject
    CursoOfertadoRepository cursoOfertadoRepository;

    @Inject
    EstudianteRepository estudianteRepository;

    @Transactional
    public Matricula execute(Long matriculaId) {

        // 1. Obtener matrícula
        Matricula matricula = matriculaRepository.findByIdOptional(matriculaId)
                .orElseThrow(() -> new NotFoundException("Matrícula no encontrada con ID: " + matriculaId));

        // 2. Retirar
        matricula.retirar();

        // 3. Actualizar créditos del estudiante
        Estudiante estudiante = matricula.getEstudiante();
        Integer creditosActuales = estudiante.getCreditosCursando() != null ? estudiante.getCreditosCursando() : 0;
        Integer creditosCurso = matricula.getCreditosMatriculados() != null ? matricula.getCreditosMatriculados() : 0;
        estudiante.setCreditosCursando(Math.max(0, creditosActuales - creditosCurso));
        estudianteRepository.persist(estudiante);

        // 4. Liberar cupo
        CursoOfertado cursoOfertado = matricula.getCursoOfertado();
        cursoOfertado.aumentarVacantes();
        cursoOfertadoRepository.persist(cursoOfertado);

        // 5. Persistir
        matriculaRepository.persist(matricula);

        return matricula;
    }
}
