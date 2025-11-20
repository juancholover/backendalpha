package upeu.edu.pe.academic.application.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PlanAcademicoResponseDTO {

    private Long id;

    // Información del programa académico
    private Long programaAcademicoId;
    private String programaAcademicoCodigo;
    private String programaAcademicoNombre;

    // Información del plan
    private String codigo;
    private String version;
    private String nombre;
    private String resolucionAprobacion;
    private LocalDate fechaAprobacion;
    private LocalDate fechaVigenciaInicio;
    private LocalDate fechaVigenciaFin;
    private Integer creditosTotales;
    private String estado;

    // Auditoría
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
