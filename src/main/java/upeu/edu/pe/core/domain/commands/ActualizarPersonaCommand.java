package upeu.edu.pe.core.domain.commands;

/**
 * Comando para actualizar una persona.
 */
public record ActualizarPersonaCommand(
        Long id,
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
    public ActualizarPersonaCommand {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la persona es obligatorio");
        }
        if (nombres == null || nombres.isBlank()) {
            throw new IllegalArgumentException("Los nombres son obligatorios");
        }
        if (apellidoPaterno == null || apellidoPaterno.isBlank()) {
            throw new IllegalArgumentException("El apellido paterno es obligatorio");
        }
        if (numeroDocumento == null || numeroDocumento.isBlank()) {
            throw new IllegalArgumentException("El número de documento es obligatorio");
        }
    }
}
