
package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.CursoOfertado;
import upeu.edu.pe.academic.domain.entities.Estudiante;
import upeu.edu.pe.academic.domain.entities.Matricula;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.academic.domain.repositories.CursoOfertadoRepository;
import upeu.edu.pe.academic.domain.repositories.EstudianteRepository;
import upeu.edu.pe.academic.domain.repositories.MatriculaRepository;
import upeu.edu.pe.academic.domain.repositories.UniversidadRepository;
import upeu.edu.pe.shared.exceptions.DuplicateResourceException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class MatricularEstudianteUseCase {

    private final MatriculaRepository matriculaRepository;
    private final EstudianteRepository estudianteRepository;
    private final CursoOfertadoRepository cursoOfertadoRepository;
    private final UniversidadRepository universidadRepository;

    @Inject
    public MatricularEstudianteUseCase(MatriculaRepository matriculaRepository,
            EstudianteRepository estudianteRepository,
            CursoOfertadoRepository cursoOfertadoRepository,
            UniversidadRepository universidadRepository) {
        this.matriculaRepository = matriculaRepository;
        this.estudianteRepository = estudianteRepository;
        this.cursoOfertadoRepository = cursoOfertadoRepository;
        this.universidadRepository = universidadRepository;
    }

    public Matricula execute(Long universidadId, Long estudianteId, Long cursoOfertadoId, String tipoMatricula) {

        Universidad universidad = universidadRepository.findByIdOptional(universidadId)
                .orElseThrow(() -> new ResourceNotFoundException("Universidad", "id", universidadId));

        Estudiante estudiante = estudianteRepository.findByIdOptional(estudianteId)
                .filter(Estudiante::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante", "id", estudianteId));

        CursoOfertado cursoOfertado = cursoOfertadoRepository.findByIdOptional(cursoOfertadoId)
                .filter(CursoOfertado::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("CursoOfertado", "id", cursoOfertadoId));

        // Validar si ya está matriculado
        if (matriculaRepository.existsByEstudianteAndCurso(estudianteId, cursoOfertadoId)) {
            throw new DuplicateResourceException("Matricula", "estudianteId + cursoOfertadoId",
                    estudianteId + " + " + cursoOfertadoId);
        }

        // Validar cupo
        if (!cursoOfertado.hayCupoDisponible()) {
            throw new IllegalStateException("No hay vacantes disponibles o el curso no está abierto");
        }

        // ==================== VALIDACIONES DE CRÉDITOS SaaS ====================

        // 1. Obtener créditos del curso desde PlanCurso
        Integer creditosCurso = cursoOfertado.getPlanCurso().getCreditos();

        // 2. Validar límite de créditos por ciclo (desde PlanAcademico de la carrera)
        // Note: Assuming PlanAcademico is accessible via PlanCurso
        var planAcademico = cursoOfertado.getPlanCurso().getPlanAcademico();
        Integer creditosActuales = estudiante.getCreditosCursando() != null ? estudiante.getCreditosCursando() : 0;
        Integer nuevoTotalCreditos = creditosActuales + creditosCurso;

        if (planAcademico != null && planAcademico.getCreditosMaximosPorCiclo() != null &&
                nuevoTotalCreditos > planAcademico.getCreditosMaximosPorCiclo()) {
            throw new IllegalStateException(
                    "El estudiante excedería el límite de créditos por ciclo de su carrera. " +
                            "Actual: " + creditosActuales + ", Curso: " + creditosCurso +
                            ", Máximo permitido: " + planAcademico.getCreditosMaximosPorCiclo());
        }

        // 3. Validar límite de estudiantes de la universidad (Plan SaaS)
        if (universidad.haExcedidoLimiteEstudiantes()) {
            throw new IllegalStateException(
                    "La universidad ha excedido su límite de estudiantes activos según su plan SaaS (" +
                            universidad.getPlan() + ")");
        }

        // 4. Validar que la universidad esté activa
        if (!universidad.estaActiva()) {
            throw new IllegalStateException(
                    "La universidad no está activa. Estado: " + universidad.getEstado());
        }

        // Crear matrícula
        Matricula matricula = Matricula.crear(universidad, estudiante, cursoOfertado, tipoMatricula);

        // Actualizar créditos del estudiante
        estudiante.setCreditosCursando(nuevoTotalCreditos);
        estudianteRepository.persist(estudiante);

        // Reducir vacantes
        cursoOfertado.reducirVacantes();

        matriculaRepository.persist(matricula);
        cursoOfertadoRepository.persist(cursoOfertado); // Actualizar vacantes

        return matricula;
    }
}
