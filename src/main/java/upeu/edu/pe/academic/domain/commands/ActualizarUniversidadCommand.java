package upeu.edu.pe.academic.domain.commands;

import java.util.Objects;

public record ActualizarUniversidadCommand(
    Long id,
    String nombre,
    String tipo,
    String plan,
    String dominio,
    String website,
    String estado,
    Integer maxEstudiantes,
    Integer maxDocentes
) {
    public ActualizarUniversidadCommand {
        Objects.requireNonNull(id, "ID es requerido para actualización");
        
        if (nombre != null && nombre.isBlank()) {
            throw new IllegalArgumentException("Nombre no puede estar vacío");
        }
    }
}
