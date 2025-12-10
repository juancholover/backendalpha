package upeu.edu.pe.enrollment.domain.commands;

/**
 * Comando para crear un curso ofertado.
 */
public record CrearCursoOfertadoCommand(
        Long planCursoId,
        Long periodoAcademicoId,
        Long profesorId,
        Long localizacionId,
        Long modalidadId,
        String codigoSeccion,
        Integer capacidadMaxima,
        String estado) {
    public CrearCursoOfertadoCommand {
        if (planCursoId == null) {
            throw new IllegalArgumentException("El ID del plan-curso es obligatorio");
        }
        if (periodoAcademicoId == null) {
            throw new IllegalArgumentException("El ID del período académico es obligatorio");
        }
        if (codigoSeccion == null || codigoSeccion.isBlank()) {
            throw new IllegalArgumentException("El código de sección es obligatorio");
        }
        if (capacidadMaxima == null || capacidadMaxima < 1) {
            throw new IllegalArgumentException("La capacidad máxima debe ser mayor a 0");
        }
        if (modalidadId == null) {
            throw new IllegalArgumentException("El ID de la modalidad es obligatorio");
        }
    }
}
