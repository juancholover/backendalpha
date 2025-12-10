package upeu.edu.pe.shared.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Evento: Se actualizó el promedio ponderado de un estudiante.
 * El Contexto Financiero puede escuchar esto para evaluar becas automáticamente.
 */
@Data
@AllArgsConstructor
public class PromedioEstudianteActualizadoEvent {
    private Long estudianteId;
    private BigDecimal promedioAnterior;
    private BigDecimal promedioNuevo;
    private Integer creditosAprobados;
    private LocalDateTime timestamp;
    
    public PromedioEstudianteActualizadoEvent(Long estudianteId, BigDecimal anterior, BigDecimal nuevo, Integer creditos) {
        this.estudianteId = estudianteId;
        this.promedioAnterior = anterior;
        this.promedioNuevo = nuevo;
        this.creditosAprobados = creditos;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Determina si el estudiante merece beca por excelencia (promedio >= 16)
     */
    public boolean mereceBecaExcelencia() {
        return promedioNuevo.compareTo(new BigDecimal("16.00")) >= 0;
    }
}
