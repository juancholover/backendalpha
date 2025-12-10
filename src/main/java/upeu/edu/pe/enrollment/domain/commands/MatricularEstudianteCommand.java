package upeu.edu.pe.enrollment.domain.commands;

/**
 * Comando para matricular un estudiante en un curso.
 */
public record MatricularEstudianteCommand(
        Long estudianteId,
        Long seccionId,
        String tipoMatricula,
        String observaciones) {
    public MatricularEstudianteCommand {
        if (estudianteId == null) {
            throw new IllegalArgumentException("El ID del estudiante es obligatorio");
        }
        if (seccionId == null) {
            throw new IllegalArgumentException("El ID de la sección es obligatorio");
        }
    }
}
