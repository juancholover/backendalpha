package upeu.edu.pe.people.domain.commands;

/**
 * Comando para crear un profesor.
 */
public record CrearProfesorCommand(
        Long empleadoId,
        String gradoAcademico,
        String categoriaDocente,
        String dedicacion,
        String codigoRenacyt,
        String especialidad) {
    public CrearProfesorCommand {
        if (empleadoId == null) {
            throw new IllegalArgumentException("El ID del empleado es obligatorio");
        }
        if (gradoAcademico == null || gradoAcademico.isBlank()) {
            throw new IllegalArgumentException("El grado académico es obligatorio");
        }
        if (categoriaDocente == null || categoriaDocente.isBlank()) {
            throw new IllegalArgumentException("La categoría docente es obligatoria");
        }
    }
}
