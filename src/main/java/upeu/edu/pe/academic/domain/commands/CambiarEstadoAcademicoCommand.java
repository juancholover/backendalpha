package upeu.edu.pe.academic.domain.commands;

import java.util.List;
import java.util.Objects;


public record CambiarEstadoAcademicoCommand(
    Long estudianteId,
    String nuevoEstado
) {
    private static final List<String> ESTADOS_VALIDOS = 
        List.of("ACTIVO", "RETIRADO", "EGRESADO", "GRADUADO", "SUSPENDIDO", "LICENCIA");
    
    public CambiarEstadoAcademicoCommand {
        Objects.requireNonNull(estudianteId, "ID de estudiante es requerido");
        Objects.requireNonNull(nuevoEstado, "Nuevo estado es requerido");
        
        if (!ESTADOS_VALIDOS.contains(nuevoEstado.toUpperCase())) {
            throw new IllegalArgumentException(
                "Estado inválido. Debe ser uno de: " + String.join(", ", ESTADOS_VALIDOS)
            );
        }
    }
}
