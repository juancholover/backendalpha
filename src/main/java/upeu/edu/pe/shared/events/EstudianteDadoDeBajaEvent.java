package upeu.edu.pe.shared.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Evento: Un estudiante fue dado de baja (desmatriculado, retirado, expulsado).
 */
@Data
@AllArgsConstructor
public class EstudianteDadoDeBajaEvent {
    private Long estudianteId;
    private String motivo; // "RETIRO_VOLUNTARIO", "BAJO_RENDIMIENTO", "DISCIPLINARIO"
    private LocalDateTime fechaBaja;
    private LocalDateTime timestamp;

    public EstudianteDadoDeBajaEvent(Long estudianteId, String motivo, LocalDateTime fechaBaja) {
        this.estudianteId = estudianteId;
        this.motivo = motivo;
        this.fechaBaja = fechaBaja;
        this.timestamp = LocalDateTime.now();
    }
}
