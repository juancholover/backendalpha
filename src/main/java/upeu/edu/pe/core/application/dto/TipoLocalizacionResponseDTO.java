package upeu.edu.pe.core.application.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TipoLocalizacionResponseDTO {
    private Long id;
    private String codigo;
    private String nombre;

    // Relación con padre
    private Long padreId;
    private String padreNombre;
    private String padreCodigo;

    // Propiedades jerárquicas
    private Integer nivelJerarquia;
    private Boolean permiteAsignacion;

    // Auditoría
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
