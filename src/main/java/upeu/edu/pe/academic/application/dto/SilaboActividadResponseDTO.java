package upeu.edu.pe.academic.application.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SilaboActividadResponseDTO {
    private Long id;
    private Long unidadId;
    private String tipo;
    private String nombre;
    private String descripcion;
    private BigDecimal ponderacion;
    private Integer semanaProgramada;
    private String instrumentoEvaluacion;
    private String indicadores;
    private Boolean esFormativa;
    private Boolean esSumativa;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
