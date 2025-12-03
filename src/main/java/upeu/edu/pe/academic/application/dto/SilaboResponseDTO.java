package upeu.edu.pe.academic.application.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SilaboResponseDTO {
    private Long id;
    private Long cursoId;
    private String cursoNombre;
    private String cursoCodigo;
    private Long universidadId;
    private String anioAcademico;
    private Integer version;
    private String estado;
    private BigDecimal porcentajeCalidad;
    private LocalDate fechaAprobacion;
    private String aprobadoPor;
    private String observaciones;
    private String competencias;
    private String sumilla;
    private String bibliografia;
    private String metodologia;
    private String recursosDidacticos;
    private Integer totalSemanas;
    private BigDecimal porcentajeEvaluacionTotal;
    private Integer cantidadUnidades;
    private List<SilaboUnidadResponseDTO> unidades;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
