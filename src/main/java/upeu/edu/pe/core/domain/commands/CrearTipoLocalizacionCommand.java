package upeu.edu.pe.core.domain.commands;

/**
 * Comando para crear un tipo de localización.
 */
public record CrearTipoLocalizacionCommand(
        String nombre) {
    public CrearTipoLocalizacionCommand {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
    }
}
