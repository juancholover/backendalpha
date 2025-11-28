package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.CursoOfertado;
import upeu.edu.pe.academic.domain.entities.Horario;
import upeu.edu.pe.academic.domain.entities.Localizacion;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.academic.domain.repositories.CursoOfertadoRepository;
import upeu.edu.pe.academic.domain.repositories.HorarioRepository;
import upeu.edu.pe.academic.domain.repositories.LocalizacionRepository;
import upeu.edu.pe.academic.domain.repositories.UniversidadRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.time.LocalTime;
import java.util.List;

@ApplicationScoped
public class CrearHorarioUseCase {

    private final HorarioRepository horarioRepository;
    private final UniversidadRepository universidadRepository;
    private final CursoOfertadoRepository cursoOfertadoRepository;
    private final LocalizacionRepository localizacionRepository;

    @Inject
    public CrearHorarioUseCase(HorarioRepository horarioRepository,
            UniversidadRepository universidadRepository,
            CursoOfertadoRepository cursoOfertadoRepository,
            LocalizacionRepository localizacionRepository) {
        this.horarioRepository = horarioRepository;
        this.universidadRepository = universidadRepository;
        this.cursoOfertadoRepository = cursoOfertadoRepository;
        this.localizacionRepository = localizacionRepository;
    }

    public Horario execute(Long universidadId, Long cursoOfertadoId, Integer diaSemana,
            LocalTime horaInicio, LocalTime horaFin, Long localizacionId,
            String tipoSesion, String observaciones) {

        // Validar universidad
        Universidad universidad = universidadRepository.findByIdOptional(universidadId)
                .orElseThrow(() -> new ResourceNotFoundException("Universidad", "id", universidadId));

        // Validar curso ofertado
        CursoOfertado cursoOfertado = cursoOfertadoRepository.findByIdOptional(cursoOfertadoId)
                .orElseThrow(() -> new ResourceNotFoundException("CursoOfertado", "id", cursoOfertadoId));

        // Validar que el curso pertenece a la universidad
        if (!cursoOfertado.getUniversidad().getId().equals(universidadId)) {
            throw new BusinessException("El curso ofertado no pertenece a la universidad especificada");
        }

        // Validar horas
        if (!horaInicio.isBefore(horaFin)) {
            throw new BusinessException("La hora de inicio debe ser anterior a la hora de fin");
        }

        // Validar cruce de horarios para el profesor
        List<Horario> crucesProfesor = horarioRepository.findCrucesProfesor(
                cursoOfertado.getProfesor().getId(),
                diaSemana,
                horaInicio,
                horaFin);

        if (!crucesProfesor.isEmpty()) {
            throw new BusinessException(
                    "El profesor ya tiene clase el " + getNombreDia(diaSemana) +
                            " de " + horaInicio + " a " + horaFin);
        }

        Localizacion localizacion = null;
        // Validar cruce de horarios en la localización (si se especifica)
        if (localizacionId != null) {
            localizacion = localizacionRepository.findByIdOptional(localizacionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Localizacion", "id", localizacionId));

            List<Horario> crucesAula = horarioRepository.findCrucesLocalizacion(
                    localizacionId,
                    diaSemana,
                    horaInicio,
                    horaFin,
                    null);

            if (!crucesAula.isEmpty()) {
                throw new BusinessException(
                        "El aula " + localizacion.getNombre() + " ya está ocupada el " +
                                getNombreDia(diaSemana) + " de " + horaInicio + " a " + horaFin);
            }
        }

        // Validar que no exista el mismo horario
        if (horarioRepository.existeHorario(cursoOfertadoId, diaSemana, horaInicio)) {
            throw new BusinessException("Ya existe un horario para este curso en el mismo día y hora");
        }

        Horario horario = Horario.crear(universidad, cursoOfertado, diaSemana, horaInicio, horaFin,
                localizacion, tipoSesion, observaciones);

        horarioRepository.persist(horario);
        return horario;
    }

    private String getNombreDia(Integer diaSemana) {
        return switch (diaSemana) {
            case 1 -> "Lunes";
            case 2 -> "Martes";
            case 3 -> "Miércoles";
            case 4 -> "Jueves";
            case 5 -> "Viernes";
            case 6 -> "Sábado";
            case 7 -> "Domingo";
            default -> "Desconocido";
        };
    }
}
