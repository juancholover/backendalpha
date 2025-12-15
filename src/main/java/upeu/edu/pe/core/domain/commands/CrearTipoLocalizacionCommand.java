package upeu.edu.pe.core.domain.commands;

/**
 * Comando para crear un tipo de localización.
 */
public record CrearTipoLocalizacionCommand(
        String codigo,
        String nombre,
        Long padreId,
        Integer nivelJerarquia,
        Boolean permiteAsignacion) {
    public CrearTipoLocalizacionCommand {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código es obligatorio");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (nivelJerarquia == null) {
            throw new IllegalArgumentException("El nivel de jerarquía es obligatorio");
        }
    }
}
