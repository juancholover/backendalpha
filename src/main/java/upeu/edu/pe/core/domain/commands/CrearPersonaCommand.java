package upeu.edu.pe.core.domain.commands;

/**
 * Comando para crear una persona.
 */
public record CrearPersonaCommand(
        String nombres,
        String apellidoPaterno,
        String apellidoMaterno,
        String tipoDocumento,
        String numeroDocumento,
        String email,
        String telefono,
        String direccion,
        String genero,
        java.time.LocalDate fechaNacimiento) {
    public CrearPersonaCommand {
        if (nombres == null || nombres.isBlank()) {
            throw new IllegalArgumentException("Los nombres son obligatorios");
        }
        if (apellidoPaterno == null || apellidoPaterno.isBlank()) {
            throw new IllegalArgumentException("El apellido paterno es obligatorio");
        }
        if (tipoDocumento == null || tipoDocumento.isBlank()) {
            throw new IllegalArgumentException("El tipo de documento es obligatorio");
        }
        if (numeroDocumento == null || numeroDocumento.isBlank()) {
            throw new IllegalArgumentException("El número de documento es obligatorio");
        }
    }
}
