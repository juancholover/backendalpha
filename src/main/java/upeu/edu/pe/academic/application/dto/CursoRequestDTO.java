package upeu.edu.pe.academic.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CursoRequestDTO {

    @NotNull(message = "El ID de la universidad es obligatorio")
    private Long universidadId;

    @NotNull(message = "El ID del plan académico es obligatorio")
    private Long planAcademicoId;

    @NotBlank(message = "El código del curso es obligatorio")
    @Size(max = 20, message = "El código del curso no puede exceder 20 caracteres")
    private String codigoCurso;

    @NotBlank(message = "El nombre del curso es obligatorio")
    @Size(max = 200, message = "El nombre del curso no puede exceder 200 caracteres")
    private String nombre;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;

    @Min(value = 0, message = "Las horas teóricas no pueden ser negativas")
    @Max(value = 20, message = "Las horas teóricas no pueden exceder 20")
    private Integer horasTeoricas;

    @Min(value = 0, message = "Las horas prácticas no pueden ser negativas")
    @Max(value = 20, message = "Las horas prácticas no pueden exceder 20")
    private Integer horasPracticas;

    @Min(value = 0, message = "Las horas semanales no pueden ser negativas")
    @Max(value = 40, message = "Las horas semanales no pueden exceder 40")
    private Integer horasSemanales;

    @Size(max = 50, message = "El tipo de curso no puede exceder 50 caracteres")
    @Pattern(regexp = "OBLIGATORIO|ELECTIVO|LIBRE", message = "El tipo de curso debe ser OBLIGATORIO, ELECTIVO o LIBRE")
    private String tipoCurso;

    @Size(max = 100, message = "El área curricular no puede exceder 100 caracteres")
    private String areaCurricular;

    @Size(max = 255, message = "La URL del sílabo no puede exceder 255 caracteres")
    private String silaboUrl;

    // NOTA: creditos y ciclo están en PlanAcademico (varían por programa)
    // NOTA: prerequisitos están en RequisitoCurso (con universidad_id)
}
