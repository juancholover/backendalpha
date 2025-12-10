package upeu.edu.pe.assessment.domain.exceptions;

import upeu.edu.pe.shared.exceptions.DuplicateResourceException;

import java.time.LocalDate;

/**
 * Excepción lanzada cuando ya existe un registro de asistencia para el mismo
 * estudiante, horario y fecha.
 */
public class AsistenciaDuplicadaException extends DuplicateResourceException {

    private final Long estudianteId;
    private final Long horarioId;
    private final LocalDate fecha;

    public AsistenciaDuplicadaException(Long estudianteId, Long horarioId, LocalDate fecha) {
        super(String.format(
                "Ya existe un registro de asistencia para el estudiante %d en el horario %d para la fecha %s",
                estudianteId, horarioId, fecha));
        this.estudianteId = estudianteId;
        this.horarioId = horarioId;
        this.fecha = fecha;
    }

    public AsistenciaDuplicadaException(LocalDate fecha) {
        super(String.format("Ya existe un registro de asistencia para el estudiante en la fecha %s", fecha));
        this.estudianteId = null;
        this.horarioId = null;
        this.fecha = fecha;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public Long getHorarioId() {
        return horarioId;
    }

    public LocalDate getFecha() {
        return fecha;
    }
}
