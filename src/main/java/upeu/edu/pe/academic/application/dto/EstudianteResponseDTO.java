package upeu.edu.pe.academic.application.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EstudianteResponseDTO {

    private Long id;
    
    // Información de persona
    private Long personaId;
    private String nombreCompleto;
    private String numeroDocumento;
    private String email;
    
    // Información del programa
    private Long programaAcademicoId;
    private String programaNombre;
    private String programaCodigo;
    
    // Información del estudiante
    private String codigoEstudiante;
    private LocalDate fechaIngreso;
    private Integer cicloActual;
    private Integer creditosAprobados;
    private BigDecimal promedioPonderado;
    private String modalidadIngreso;
    private String estadoAcademico;
    private String tipoEstudiante;
    
    // Auditoría
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
