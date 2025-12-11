package upeu.edu.pe.enrollment.domain.commands;

/**
 * Comando para crear una localización.
 */
public record CrearLocalizacionCommand(
        Long tipoLocalizacionId,
        String codigo,
        String nombre,
        String direccion,
        String telefono,
        String email,
        Boolean esPrincipal) {
    public CrearLocalizacionCommand {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código es obligatorio");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
    }
}
