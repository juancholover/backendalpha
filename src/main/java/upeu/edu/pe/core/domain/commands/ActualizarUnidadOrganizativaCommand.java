package upeu.edu.pe.core.domain.commands;

/**
 * Comando para actualizar una unidad organizativa.
 */
public record ActualizarUnidadOrganizativaCommand(
        Long id,
        Long tipoUnidadId,
        String codigo,
        String nombre,
        String abreviatura,
        String descripcion,
        Long unidadPadreId,
        Long localizacionId) {
    public ActualizarUnidadOrganizativaCommand {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la unidad es obligatorio");
        }
        if (tipoUnidadId == null) {
            throw new IllegalArgumentException("El tipo de unidad es obligatorio");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
    }
}
