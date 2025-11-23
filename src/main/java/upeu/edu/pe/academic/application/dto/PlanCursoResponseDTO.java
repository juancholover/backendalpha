package upeu.edu.pe.academic.application.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PlanCursoResponseDTO {

    private Long id;

    // Información de la universidad
    private Long universidadId;
    private String universidadNombre;

    // Información del plan académico
    private Long planAcademicoId;
    private String planAcademicoCodigo;
    private String planAcademicoNombre;
    private String planAcademicoVersion;

    // Información del curso
    private Long cursoId;
    private String cursoCodigo;
    private String cursoNombre;

    // Atributos de la relación
    private Integer creditos;
    private Integer ciclo;
    private String tipoCurso; // OBLIGATORIO, ELECTIVO
    private Boolean esObligatorio;

    // Auditoría
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
