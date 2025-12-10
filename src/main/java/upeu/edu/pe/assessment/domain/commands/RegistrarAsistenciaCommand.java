package upeu.edu.pe.assessment.domain.commands;

import java.time.LocalDate;

/**
 * Comando para registrar asistencia de un estudiante.
 */
public record RegistrarAsistenciaCommand(
        Long estudianteId,
        Long horarioId,
        LocalDate fechaClase,
        String estado,
        String observaciones,
        Integer minutosTardanza) {
    /**
     * Constructor compacto con validaciones básicas.
     */
    public RegistrarAsistenciaCommand {
        if (estudianteId == null) {
            throw new IllegalArgumentException("El ID del estudiante es obligatorio");
        }
        if (horarioId == null) {
            throw new IllegalArgumentException("El ID del horario es obligatorio");
        }
        if (fechaClase == null) {
            throw new IllegalArgumentException("La fecha de clase es obligatoria");
        }
        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }
        // Validar estados permitidos
        String estadoUpper = estado.toUpperCase();
        if (!estadoUpper.equals("PRESENTE") &&
                !estadoUpper.equals("AUSENTE") &&
                !estadoUpper.equals("TARDANZA") &&
                !estadoUpper.equals("JUSTIFICADO")) {
            throw new IllegalArgumentException(
                    "Estado inválido. Valores permitidos: PRESENTE, AUSENTE, TARDANZA, JUSTIFICADO");
        }
        // Si es tardanza, debe tener minutos
        if (estadoUpper.equals("TARDANZA") && (minutosTardanza == null || minutosTardanza <= 0)) {
            throw new IllegalArgumentException(
                    "Si el estado es TARDANZA, debe especificar los minutos de tardanza");
        }
    }

    /**
     * Constructor para marcar presente.
     */
    public static RegistrarAsistenciaCommand presente(Long estudianteId, Long horarioId, LocalDate fechaClase) {
        return new RegistrarAsistenciaCommand(estudianteId, horarioId, fechaClase, "PRESENTE", null, null);
    }

    /**
     * Constructor para marcar ausente.
     */
    public static RegistrarAsistenciaCommand ausente(Long estudianteId, Long horarioId, LocalDate fechaClase) {
        return new RegistrarAsistenciaCommand(estudianteId, horarioId, fechaClase, "AUSENTE", null, null);
    }

    /**
     * Constructor para marcar tardanza.
     */
    public static RegistrarAsistenciaCommand tardanza(Long estudianteId, Long horarioId, LocalDate fechaClase,
            int minutos) {
        return new RegistrarAsistenciaCommand(estudianteId, horarioId, fechaClase, "TARDANZA", null, minutos);
    }
}
