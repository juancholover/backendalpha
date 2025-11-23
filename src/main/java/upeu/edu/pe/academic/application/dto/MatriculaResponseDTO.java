package upeu.edu.pe.academic.application.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MatriculaResponseDTO {

    private Long id;

    // Información del estudiante
    private Long estudianteId;
    private String estudianteNombre;
    private String estudianteApellido;
    private String estudianteCodigo;

    // Información de la sección
    private Long seccionId;
    private String seccionCodigo;
    
    // Información del curso (desde seccion.planAcademico)
    private Long cursoId;
    private String cursoNombre;
    private String cursoCodigo;

    // Información del período académico (desde seccion.periodoAcademico)
    private Long periodoAcademicoId;
    private String periodoAcademicoNombre;
    private String periodoAcademicoCodigo;

    // Información del profesor (desde seccion.profesor)
    private Long profesorId;
    private String profesorNombre;
    private String profesorApellido;

    // Datos de la matrícula
    private LocalDate fechaMatricula;
    private String tipoMatricula;

    // ==================== CONTROL DE CRÉDITOS ====================
    private Integer creditosMatriculados;

    private String estadoMatricula;
    private LocalDate fechaRetiro;
    private BigDecimal notaFinal;
    private String estadoAprobacion;
    private Integer inasistencias;

    // Auditoría
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
