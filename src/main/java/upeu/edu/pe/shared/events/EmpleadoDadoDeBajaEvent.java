package upeu.edu.pe.shared.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Evento de dominio: Un empleado fue dado de baja en el Contexto RRHH.
 * Permite que otros contextos (Académico, Financiero) reaccionen sin acoplamiento directo.
 */
@Data
@AllArgsConstructor
public class EmpleadoDadoDeBajaEvent {
    private Long empleadoId;
    private LocalDateTime fechaCese;
    private String motivo; // "RENUNCIA", "DESPIDO", "JUBILACION"
    private LocalDateTime timestamp;

    public EmpleadoDadoDeBajaEvent(Long empleadoId, LocalDateTime fechaCese, String motivo) {
        this.empleadoId = empleadoId;
        this.fechaCese = fechaCese;
        this.motivo = motivo;
        this.timestamp = LocalDateTime.now();
    }
}
