package upeu.edu.pe.core.domain.commands;

/**
 * Comando para crear un tipo de unidad.
 */
public record CrearTipoUnidadCommand(
        String nombre,
        String descripcion,
        Integer nivel) {
    public CrearTipoUnidadCommand {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
    }
}
