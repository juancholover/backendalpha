package upeu.edu.pe.assessment.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.assessment.domain.commands.RegistrarAsistenciaCommand;
import upeu.edu.pe.assessment.domain.entities.AsistenciaAlumno;
import upeu.edu.pe.assessment.domain.repositories.AsistenciaAlumnoRepository;
import upeu.edu.pe.enrollment.domain.entities.Horario;
import upeu.edu.pe.enrollment.domain.repositories.HorarioRepository;
import upeu.edu.pe.people.domain.entities.Estudiante;
import upeu.edu.pe.people.domain.repositories.EstudianteRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Caso de Uso: Registrar asistencia de un estudiante a una clase.
 * 
 * Reglas de negocio:
 * - El estudiante debe existir y estar activo
 * - El horario debe existir
 * - La fecha debe corresponder al día de la semana del horario
 * - No puede existir registro duplicado de asistencia
 * 
 * @author Sistema UPeU
 */
@ApplicationScoped
public class RegistrarAsistenciaUseCase {

    @Inject
    AsistenciaAlumnoRepository asistenciaRepository;

    @Inject
    EstudianteRepository estudianteRepository;

    @Inject
    HorarioRepository horarioRepository;

    /**
     * Ejecuta el caso de uso para registrar asistencia.
     * 
     * @param command Datos de la asistencia a registrar
     * @return La asistencia registrada
     * @throws NotFoundException si el estudiante o horario no existen
     * @throws BusinessException si hay registro duplicado o fecha incorrecta
     */
    @Transactional
    public AsistenciaAlumno execute(RegistrarAsistenciaCommand command) {

        // 1. Validar y obtener el estudiante
        Estudiante estudiante = estudianteRepository.findByIdOptional(command.estudianteId())
                .orElseThrow(() -> new NotFoundException(
                        "Estudiante no encontrado con ID: " + command.estudianteId()));

        // 2. Validar y obtener el horario
        Horario horario = horarioRepository.findByIdOptional(command.horarioId())
                .orElseThrow(() -> new NotFoundException(
                        "Horario no encontrado con ID: " + command.horarioId()));

        // 3. Validar que la fecha corresponda al día del horario
        validarDiaSemana(command.fechaClase(), horario);

        // 4. Verificar que no exista registro duplicado
        if (asistenciaRepository.existsByEstudianteHorarioFecha(
                command.estudianteId(), command.horarioId(), command.fechaClase())) {
            throw new BusinessException(
                    String.format("Ya existe un registro de asistencia para el estudiante en la fecha %s.",
                            command.fechaClase()));
        }

        // 5. Crear y persistir la asistencia
        AsistenciaAlumno asistencia = new AsistenciaAlumno();
        asistencia.setEstudiante(estudiante);
        asistencia.setHorario(horario);
        asistencia.setFechaClase(command.fechaClase());
        asistencia.setEstado(command.estado().toUpperCase());
        asistencia.setObservaciones(command.observaciones());
        asistencia.setMinutosTardanza(command.minutosTardanza());

        asistenciaRepository.persist(asistencia);

        return asistencia;
    }

    /**
     * Valida que la fecha de clase corresponda al día de la semana del horario.
     */
    private void validarDiaSemana(LocalDate fechaClase, Horario horario) {
        DayOfWeek diaSemanaFecha = fechaClase.getDayOfWeek();
        int diaSemanaHorario = horario.getDiaSemana(); // 1=Lunes, 7=Domingo

        // Convertir de Java DayOfWeek (1=Lunes) a nuestro formato
        int diaSemanaFechaNum = diaSemanaFecha.getValue();

        if (diaSemanaFechaNum != diaSemanaHorario) {
            String nombreDiaEsperado = obtenerNombreDia(diaSemanaHorario);
            String nombreDiaRecibido = obtenerNombreDia(diaSemanaFechaNum);
            throw new BusinessException(
                    String.format("La fecha %s es %s, pero el horario corresponde a %s.",
                            fechaClase, nombreDiaRecibido, nombreDiaEsperado));
        }
    }

    /**
     * Obtiene el nombre del día en español.
     */
    private String obtenerNombreDia(int diaSemana) {
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
