package upeu.edu.pe.people.domain.commands;

/**
 * Comando para actualizar un profesor.
 */
public record ActualizarProfesorCommand(
        Long id,
        String gradoAcademico,
        String categoriaDocente,
        String dedicacion,
        String codigoRenacyt,
        String especialidad) {
    public ActualizarProfesorCommand {
        if (id == null) {
            throw new IllegalArgumentException("El ID del profesor es obligatorio");
        }
        if (gradoAcademico == null || gradoAcademico.isBlank()) {
            throw new IllegalArgumentException("El grado académico es obligatorio");
        }
        if (categoriaDocente == null || categoriaDocente.isBlank()) {
            throw new IllegalArgumentException("La categoría docente es obligatoria");
        }
    }
}
