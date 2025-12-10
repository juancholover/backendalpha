package upeu.edu.pe.assessment.domain.commands;

/**
 * Comando para crear un criterio de evaluación.
 * Objeto inmutable (record) que contiene los datos necesarios para la
 * operación.
 */
public record CrearCriterioCommand(
        Long seccionId,
        String nombre,
        Integer peso,
        String tipoEvaluacion,
        Integer notaMaxima,
        Integer notaMinimaAprobatoria,
        Boolean esRecuperable,
        String descripcion) {
    /**
     * Constructor compacto con validaciones básicas.
     */
    public CrearCriterioCommand {
        if (seccionId == null) {
            throw new IllegalArgumentException("El ID de sección es obligatorio");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (peso == null || peso < 0 || peso > 100) {
            throw new IllegalArgumentException("El peso debe estar entre 0 y 100");
        }
        if (tipoEvaluacion == null || tipoEvaluacion.isBlank()) {
            throw new IllegalArgumentException("El tipo de evaluación es obligatorio");
        }
    }

    /**
     * Constructor con valores por defecto.
     */
    public static CrearCriterioCommand of(Long seccionId, String nombre, Integer peso, String tipoEvaluacion) {
        return new CrearCriterioCommand(
                seccionId,
                nombre,
                peso,
                tipoEvaluacion,
                20, // nota máxima por defecto
                11, // nota mínima por defecto
                false, // no recuperable por defecto
                null);
    }
}
