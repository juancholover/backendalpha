package upeu.edu.pe.curriculum.domain.commands;

/**
 * Comando para crear un plan-curso (asignar curso a plan).
 */
public record CrearPlanCursoCommand(
        Long planAcademicoId,
        Long cursoId,
        Integer creditos,
        Integer ciclo,
        String tipoCurso,
        Boolean esObligatorio) {
    public CrearPlanCursoCommand {
        if (planAcademicoId == null) {
            throw new IllegalArgumentException("El ID del plan académico es obligatorio");
        }
        if (cursoId == null) {
            throw new IllegalArgumentException("El ID del curso es obligatorio");
        }
    }
}
