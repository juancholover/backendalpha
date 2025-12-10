package upeu.edu.pe.core.domain.commands;

/**
 * Comando para crear una unidad organizativa.
 */
public record CrearUnidadOrganizativaCommand(
        Long tipoUnidadId,
        String codigo,
        String nombre,
        String abreviatura,
        String descripcion,
        Long unidadPadreId,
        Long localizacionId) {
    public CrearUnidadOrganizativaCommand {
        if (tipoUnidadId == null) {
            throw new IllegalArgumentException("El tipo de unidad es obligatorio");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
    }
}
