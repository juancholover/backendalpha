package upeu.edu.pe.core.domain.commands;

/**
 * Comando para actualizar un tipo de unidad.
 */
public record ActualizarTipoUnidadCommand(
        Long id,
        String nombre,
        String descripcion,
        Integer nivel) {
    public ActualizarTipoUnidadCommand {
        if (id == null) {
            throw new IllegalArgumentException("El ID es obligatorio");
        }
    }
}
