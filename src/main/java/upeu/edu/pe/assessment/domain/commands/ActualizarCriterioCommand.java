package upeu.edu.pe.assessment.domain.commands;

/**
 * Comando para actualizar un criterio de evaluación existente.
 */
public record ActualizarCriterioCommand(
        Long criterioId,
        Long seccionId,
        String nombre,
        Integer peso,
        String tipoEvaluacion,
        Integer notaMaxima,
        Integer notaMinimaAprobatoria,
        Boolean esRecuperable,
        Integer orden,
        String estado,
        String descripcion) {
    /**
     * Constructor compacto con validaciones básicas.
     */
    public ActualizarCriterioCommand {
        if (criterioId == null) {
            throw new IllegalArgumentException("El ID del criterio es obligatorio");
        }
        if (seccionId == null) {
            throw new IllegalArgumentException("El ID de sección es obligatorio");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (peso == null || peso < 0 || peso > 100) {
            throw new IllegalArgumentException("El peso debe estar entre 0 y 100");
        }
    }
}
