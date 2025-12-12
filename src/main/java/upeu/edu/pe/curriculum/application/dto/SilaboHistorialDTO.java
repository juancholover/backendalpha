package upeu.edu.pe.curriculum.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para transferencia de datos del historial de sílabos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SilaboHistorialDTO {
    private Long id;
    private Long silaboId;
    private Integer versionAnterior;
    private Integer versionNueva;
    private LocalDateTime fecha;
    private String accion;
    private String usuario;
    private String comentarios;
}
