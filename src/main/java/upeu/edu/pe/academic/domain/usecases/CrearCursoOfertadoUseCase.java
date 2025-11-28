package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.*;
import upeu.edu.pe.academic.domain.repositories.*;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class CrearCursoOfertadoUseCase {

    private final CursoOfertadoRepository cursoOfertadoRepository;
    private final UniversidadRepository universidadRepository;
    private final PlanCursoRepository planCursoRepository;
    private final PeriodoAcademicoRepository periodoAcademicoRepository;
    private final ProfesorRepository profesorRepository;
    private final LocalizacionRepository localizacionRepository;

    @Inject
    public CrearCursoOfertadoUseCase(CursoOfertadoRepository cursoOfertadoRepository,
            UniversidadRepository universidadRepository,
            PlanCursoRepository planCursoRepository,
            PeriodoAcademicoRepository periodoAcademicoRepository,
            ProfesorRepository profesorRepository,
            LocalizacionRepository localizacionRepository) {
        this.cursoOfertadoRepository = cursoOfertadoRepository;
        this.universidadRepository = universidadRepository;
        this.planCursoRepository = planCursoRepository;
        this.periodoAcademicoRepository = periodoAcademicoRepository;
        this.profesorRepository = profesorRepository;
        this.localizacionRepository = localizacionRepository;
    }

    public CursoOfertado execute(Long universidadId, Long planCursoId, Long periodoAcademicoId,
            String codigoSeccion, Integer capacidadMaxima, Integer vacantesDisponibles,
            String modalidad, Long profesorId, Long localizacionId) {

        // Validar que no exista el curso ofertado
        if (cursoOfertadoRepository.existsByCodigoAndPeriodoAndPlanCurso(codigoSeccion, periodoAcademicoId,
                planCursoId)) {
            throw new BusinessException("Ya existe un curso ofertado con el código: " + codigoSeccion
                    + " para este plan-curso y período");
        }

        Universidad universidad = universidadRepository.findByIdOptional(universidadId)
                .orElseThrow(() -> new ResourceNotFoundException("Universidad", "id", universidadId));

        PlanCurso planCurso = planCursoRepository.findByIdOptional(planCursoId)
                .orElseThrow(() -> new ResourceNotFoundException("PlanCurso", "id", planCursoId));

        PeriodoAcademico periodoAcademico = periodoAcademicoRepository.findByIdOptional(periodoAcademicoId)
                .orElseThrow(() -> new ResourceNotFoundException("PeriodoAcademico", "id", periodoAcademicoId));

        // Validar vacantes disponibles
        if (vacantesDisponibles == null) {
            vacantesDisponibles = capacidadMaxima;
        } else if (vacantesDisponibles > capacidadMaxima) {
            throw new BusinessException("Las vacantes disponibles no pueden exceder la capacidad máxima");
        }

        Profesor profesor = null;
        if (profesorId != null) {
            profesor = profesorRepository.findByIdOptional(profesorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Profesor", "id", profesorId));
        }

        Localizacion localizacion = null;
        if (localizacionId != null) {
            localizacion = localizacionRepository.findByIdOptional(localizacionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Localizacion", "id", localizacionId));
        }

        CursoOfertado cursoOfertado = CursoOfertado.crear(universidad, planCurso, periodoAcademico,
                codigoSeccion, capacidadMaxima, modalidad, profesor, localizacion);

        // Override vacantes if provided explicitly different from capacity (though
        // factory sets it to capacity)
        if (!vacantesDisponibles.equals(capacidadMaxima)) {
            cursoOfertado.setVacantesDisponibles(vacantesDisponibles);
        }

        cursoOfertadoRepository.persist(cursoOfertado);
        return cursoOfertado;
    }
}
