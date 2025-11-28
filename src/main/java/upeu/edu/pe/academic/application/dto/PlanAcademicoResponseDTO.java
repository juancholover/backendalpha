package upeu.edu.pe.academic.application.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PlanAcademicoResponseDTO {

    private Long id;

    // Información de la universidad
    private Long universidadId;
    private String universidadNombre;

    // Información del programa académico
    private Long programaAcademicoId;
    private String programaAcademicoCodigo;
    private String programaAcademicoNombre;

    // Información del plan
    private String codigo;
    private String version;
    private String nombre;
    private LocalDate fechaAprobacion;
    private LocalDate fechaVigenciaInicio;
    private LocalDate fechaVigenciaFin;
    private Integer creditosTotales;

    // ==================== DISTRIBUCIÓN DE CRÉDITOS ====================
    private Integer creditosObligatorios;
    private Integer creditosElectivos;
    private Integer duracionSemestres;

    private String estado;

    // ==================== REGLAS ACADÉMICAS POR CARRERA ====================
    private Integer creditosMaximosPorCiclo; // Máximo que puede matricular por ciclo
    private Integer creditosMinimosTiempoCompleto; // Mínimo para ser tiempo completo
    private Integer duracionCicloMeses; // Duración del ciclo en meses

    // Auditoría
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
