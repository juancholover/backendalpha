package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.CursoOfertado;
import upeu.edu.pe.academic.domain.entities.Horario;
import upeu.edu.pe.academic.domain.entities.Localizacion;
import upeu.edu.pe.academic.domain.repositories.HorarioRepository;
import upeu.edu.pe.academic.domain.repositories.LocalizacionRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.time.LocalTime;
import java.util.List;

@ApplicationScoped
public class ActualizarHorarioUseCase {

    private final HorarioRepository horarioRepository;
    private final LocalizacionRepository localizacionRepository;

    @Inject
    public ActualizarHorarioUseCase(HorarioRepository horarioRepository,
            LocalizacionRepository localizacionRepository) {
        this.horarioRepository = horarioRepository;
        this.localizacionRepository = localizacionRepository;
    }

    public Horario execute(Long id, Integer diaSemana, LocalTime horaInicio, LocalTime horaFin,
            Long localizacionId, String tipoSesion, String observaciones) {

        Horario horario = horarioRepository.findByIdOptional(id)
                .filter(Horario::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Horario", "id", id));

        // Validar horas
        if (!horaInicio.isBefore(horaFin)) {
            throw new BusinessException("La hora de inicio debe ser anterior a la hora de fin");
        }

        // Validar cruce con profesor (excluyendo el horario actual)
        CursoOfertado cursoOfertado = horario.getCursoOfertado();
        List<Horario> crucesProfesor = horarioRepository.findCrucesProfesor(
                cursoOfertado.getProfesor().getId(),
                diaSemana,
                horaInicio,
                horaFin);

        // Excluir el horario actual de los cruces
        boolean tieneCruceProfesor = crucesProfesor.stream()
                .anyMatch(h -> !h.getId().equals(id));

        if (tieneCruceProfesor) {
            throw new BusinessException(
                    "El profesor ya tiene clase el " + getNombreDia(diaSemana) +
                            " de " + horaInicio + " a " + horaFin);
        }

        // Validar cruce con localización (si se especifica)
        if (localizacionId != null) {
            Localizacion localizacion = localizacionRepository.findByIdOptional(localizacionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Localizacion", "id", localizacionId));

            List<Horario> crucesAula = horarioRepository.findCrucesLocalizacion(
                    localizacionId,
                    diaSemana,
                    horaInicio,
                    horaFin,
                    id // Excluir el horario actual
            );

            if (!crucesAula.isEmpty()) {
                throw new BusinessException(
                        "El aula " + localizacion.getNombre() + " ya está ocupada el " +
                                getNombreDia(diaSemana) + " de " + horaInicio + " a " + horaFin);
            }

            horario.setLocalizacion(localizacion);
        } else {
            horario.setLocalizacion(null);
        }

        horario.setDiaSemana(diaSemana);
        horario.setHoraInicio(horaInicio);
        horario.setHoraFin(horaFin);
        horario.setTipoSesion(tipoSesion);
        horario.setObservaciones(observaciones);

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
