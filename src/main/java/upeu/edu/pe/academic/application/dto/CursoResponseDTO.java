package upeu.edu.pe.academic.application.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CursoResponseDTO {

    private Long id;

    // Información del plan académico
    private Long planAcademicoId;
    private String planAcademicoCodigo;
    private String planAcademicoNombre;
    private String programaAcademicoNombre;

    // Información del curso
    private String codigoCurso;
    private String nombre;
    private String descripcion;
    private Integer creditos;
    private Integer horasTeoricas;
    private Integer horasPracticas;
    private Integer horasSemanales;
    private Integer ciclo;
    private String tipoCurso;
    private String areaCurricular;
    private String silaboUrl;

    // Prerequisito
    private Long prerequisitoId;
    private String prerequisitoNombre;
    private String prerequisitoCodigo;

    // Auditoría
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
