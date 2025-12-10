package upeu.edu.pe.enrollment.domain.commands;

/**
 * Comando para actualizar un curso ofertado.
 */
public record ActualizarCursoOfertadoCommand(
        Long id,
        Long profesorId,
        Long localizacionId,
        String codigoSeccion,
        Integer capacidadMaxima,
        Integer vacantesDisponibles,
        String modalidad,
        String estado) {
    public ActualizarCursoOfertadoCommand {
        if (id == null) {
            throw new IllegalArgumentException("El ID del curso ofertado es obligatorio");
        }
    }
}
