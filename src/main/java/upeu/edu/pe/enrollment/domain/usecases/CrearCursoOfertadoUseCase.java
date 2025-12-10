package upeu.edu.pe.enrollment.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.curriculum.domain.entities.PlanCurso;
import upeu.edu.pe.curriculum.domain.repositories.PlanCursoRepository;
import upeu.edu.pe.enrollment.domain.commands.CrearCursoOfertadoCommand;
import upeu.edu.pe.enrollment.domain.entities.CursoOfertado;
import upeu.edu.pe.enrollment.domain.entities.Localizacion;
import upeu.edu.pe.enrollment.domain.entities.Modalidad;
import upeu.edu.pe.enrollment.domain.entities.PeriodoAcademico;
import upeu.edu.pe.enrollment.domain.repositories.CursoOfertadoRepository;
import upeu.edu.pe.enrollment.domain.repositories.LocalizacionRepository;
import upeu.edu.pe.enrollment.domain.repositories.ModalidadRepository;
import upeu.edu.pe.enrollment.domain.repositories.PeriodoAcademicoRepository;
import upeu.edu.pe.people.domain.entities.Profesor;
import upeu.edu.pe.people.domain.repositories.ProfesorRepository;
import upeu.edu.pe.shared.exceptions.DuplicateResourceException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Caso de Uso: Crear un curso ofertado.
 * 
 * Reglas de negocio:
 * - Plan-Curso debe existir
 * - Período académico debe existir
 * - Modalidad debe existir
 * - No debe existir duplicado (código + período + plan-curso)
 */
@ApplicationScoped
public class CrearCursoOfertadoUseCase {

    @Inject
    CursoOfertadoRepository cursoOfertadoRepository;

    @Inject
    PlanCursoRepository planCursoRepository;

    @Inject
    PeriodoAcademicoRepository periodoAcademicoRepository;

    @Inject
    ModalidadRepository modalidadRepository;

    @Inject
    ProfesorRepository profesorRepository;

    @Inject
    LocalizacionRepository localizacionRepository;

    @Transactional
    public CursoOfertado execute(CrearCursoOfertadoCommand command) {

        // 1. Validar duplicado
        if (cursoOfertadoRepository.existsByCodigoAndPeriodoAndPlanCurso(
                command.codigoSeccion(), command.periodoAcademicoId(), command.planCursoId())) {
            throw new DuplicateResourceException(
                    "Ya existe un curso ofertado con el código: " + command.codigoSeccion());
        }

        // 2. Obtener plan-curso
        PlanCurso planCurso = planCursoRepository.findByIdOptional(command.planCursoId())
                .orElseThrow(() -> new NotFoundException("Plan-Curso no encontrado con ID: " + command.planCursoId()));

        // 3. Obtener período académico
        PeriodoAcademico periodoAcademico = periodoAcademicoRepository.findByIdOptional(command.periodoAcademicoId())
                .orElseThrow(() -> new NotFoundException(
                        "Período académico no encontrado con ID: " + command.periodoAcademicoId()));

        // 4. Obtener modalidad
        Modalidad modalidad = modalidadRepository.findByIdOptional(command.modalidadId())
                .orElseThrow(() -> new NotFoundException("Modalidad no encontrada con ID: " + command.modalidadId()));

        // 5. Crear curso ofertado
        CursoOfertado cursoOfertado = new CursoOfertado();
        cursoOfertado.setPlanCurso(planCurso);
        cursoOfertado.setPeriodoAcademico(periodoAcademico);
        cursoOfertado.setModalidad(modalidad);
        cursoOfertado.setCodigoSeccion(command.codigoSeccion());
        cursoOfertado.setCapacidadMaxima(command.capacidadMaxima());
        cursoOfertado.setVacantesDisponibles(command.capacidadMaxima());
        cursoOfertado.setEstado(command.estado() != null ? command.estado() : "ABIERTO");

        // 6. Asignar profesor si existe
        if (command.profesorId() != null) {
            Profesor profesor = profesorRepository.findByIdOptional(command.profesorId())
                    .orElseThrow(() -> new NotFoundException("Profesor no encontrado con ID: " + command.profesorId()));
            cursoOfertado.setProfesor(profesor);
        }

        // 7. Asignar localización si existe
        if (command.localizacionId() != null) {
            Localizacion localizacion = localizacionRepository.findByIdOptional(command.localizacionId())
                    .orElseThrow(() -> new NotFoundException(
                            "Localización no encontrada con ID: " + command.localizacionId()));
            cursoOfertado.setLocalizacion(localizacion);
        }

        // 8. Persistir
        cursoOfertadoRepository.persist(cursoOfertado);

        return cursoOfertado;
    }
}
