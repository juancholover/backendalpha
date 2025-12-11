package upeu.edu.pe.core.domain.commands;

/**
 * Comando para actualizar un tipo de localización.
 */
public record ActualizarTipoLocalizacionCommand(
        Long id,
        String nombre) {
    public ActualizarTipoLocalizacionCommand {
        if (id == null) {
            throw new IllegalArgumentException("El ID es obligatorio");
        }
    }
}
