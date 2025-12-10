package upeu.edu.pe.curriculum.domain.commands;

/**
 * Comando para crear un curso.
 */
public record CrearCursoCommand(
        String codigoCurso,
        String nombreCurso,
        String tipoCurso,
        Integer horasTeoricas,
        Integer horasPracticas,
        Integer horasSemanales,
        String descripcion) {
    public CrearCursoCommand {
        if (codigoCurso == null || codigoCurso.isBlank()) {
            throw new IllegalArgumentException("El código del curso es obligatorio");
        }
        if (nombreCurso == null || nombreCurso.isBlank()) {
            throw new IllegalArgumentException("El nombre del curso es obligatorio");
        }
    }
}
