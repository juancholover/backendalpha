package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.*;
import upeu.edu.pe.academic.domain.repositories.*;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class ActualizarCursoOfertadoUseCase {

    private final CursoOfertadoRepository cursoOfertadoRepository;
    private final UniversidadRepository universidadRepository;
    private final PlanCursoRepository planCursoRepository;
    private final PeriodoAcademicoRepository periodoAcademicoRepository;
    private final ProfesorRepository profesorRepository;
    private final LocalizacionRepository localizacionRepository;

    @Inject
    public ActualizarCursoOfertadoUseCase(CursoOfertadoRepository cursoOfertadoRepository,
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

    public CursoOfertado execute(Long id, Long universidadId, Long planCursoId, Long periodoAcademicoId,
            String codigoSeccion, Integer capacidadMaxima, Integer vacantesDisponibles,
            String modalidad, Long profesorId, Long localizacionId, String estado, String observaciones) {

        CursoOfertado cursoOfertado = cursoOfertadoRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("CursoOfertado", "id", id));

        // Validar cambios de relaciones
        if (!cursoOfertado.getUniversidad().getId().equals(universidadId)) {
            Universidad universidad = universidadRepository.findByIdOptional(universidadId)
                    .orElseThrow(() -> new ResourceNotFoundException("Universidad", "id", universidadId));
            cursoOfertado.setUniversidad(universidad);
        }

        if (!cursoOfertado.getPlanCurso().getId().equals(planCursoId)) {
            PlanCurso planCurso = planCursoRepository.findByIdOptional(planCursoId)
                    .orElseThrow(() -> new ResourceNotFoundException("PlanCurso", "id", planCursoId));
            cursoOfertado.setPlanCurso(planCurso);
        }

        if (!cursoOfertado.getPeriodoAcademico().getId().equals(periodoAcademicoId)) {
            PeriodoAcademico periodoAcademico = periodoAcademicoRepository.findByIdOptional(periodoAcademicoId)
                    .orElseThrow(() -> new ResourceNotFoundException("PeriodoAcademico", "id", periodoAcademicoId));
            cursoOfertado.setPeriodoAcademico(periodoAcademico);
        }

        // Validar profesor
        if (profesorId != null) {
            if (cursoOfertado.getProfesor() == null || !cursoOfertado.getProfesor().getId().equals(profesorId)) {
                Profesor profesor = profesorRepository.findByIdOptional(profesorId)
                        .orElseThrow(() -> new ResourceNotFoundException("Profesor", "id", profesorId));
                cursoOfertado.setProfesor(profesor);
            }
        } else {
            cursoOfertado.setProfesor(null);
        }

        // Validar localización
        if (localizacionId != null) {
            if (cursoOfertado.getLocalizacion() == null
                    || !cursoOfertado.getLocalizacion().getId().equals(localizacionId)) {
                Localizacion localizacion = localizacionRepository.findByIdOptional(localizacionId)
                        .orElseThrow(() -> new ResourceNotFoundException("Localizacion", "id", localizacionId));
                cursoOfertado.setLocalizacion(localizacion);
            }
        } else {
            cursoOfertado.setLocalizacion(null);
        }

        // Validar vacantes
        if (vacantesDisponibles != null && vacantesDisponibles > capacidadMaxima) {
            throw new BusinessException("Las vacantes disponibles no pueden exceder la capacidad máxima");
        }

        cursoOfertado.setCodigoSeccion(codigoSeccion);
        cursoOfertado.setCapacidadMaxima(capacidadMaxima);
        if (vacantesDisponibles != null) {
            cursoOfertado.setVacantesDisponibles(vacantesDisponibles);
        }
        cursoOfertado.setModalidad(modalidad);
        cursoOfertado.setEstado(estado);
        cursoOfertado.setObservaciones(observaciones);

        cursoOfertadoRepository.persist(cursoOfertado);
        return cursoOfertado;
    }
}
