package upeu.edu.pe.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para estadísticas del sistema de backups
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackupEstadisticasDTO {
    
    private Integer totalBackups;
    private Integer exitosos;
    private Integer errores;
    private Double espacioTotalGB;
    private LocalDateTime ultimoBackupExitoso;
}
