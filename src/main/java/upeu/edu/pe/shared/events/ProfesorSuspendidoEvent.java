package upeu.edu.pe.shared.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Evento: Un profesor fue suspendido (no puede dictar clases temporalmente).
 */
@Data
@AllArgsConstructor
public class ProfesorSuspendidoEvent {
    private Long profesorId;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin; // Nullable si es indefinida
    private String motivo;
    private LocalDateTime timestamp;

    public ProfesorSuspendidoEvent(Long profesorId, LocalDateTime inicio, LocalDateTime fin, String motivo) {
        this.profesorId = profesorId;
        this.fechaInicio = inicio;
        this.fechaFin = fin;
        this.motivo = motivo;
        this.timestamp = LocalDateTime.now();
    }
}
