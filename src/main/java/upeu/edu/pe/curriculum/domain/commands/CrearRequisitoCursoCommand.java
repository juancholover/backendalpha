package upeu.edu.pe.curriculum.domain.commands;

/**
 * Comando para crear un requisito de curso.
 */
public record CrearRequisitoCursoCommand(
        Long cursoId,
        Long cursoRequisitoId,
        String tipoRequisito) {
    public CrearRequisitoCursoCommand {
        if (cursoId == null) {
            throw new IllegalArgumentException("El ID del curso es obligatorio");
        }
        if (cursoRequisitoId == null) {
            throw new IllegalArgumentException("El ID del curso requisito es obligatorio");
        }
        if (cursoId.equals(cursoRequisitoId)) {
            throw new IllegalArgumentException("Un curso no puede ser requisito de sí mismo");
        }
    }
}
