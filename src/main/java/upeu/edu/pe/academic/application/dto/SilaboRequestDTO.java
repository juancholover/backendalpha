package upeu.edu.pe.academic.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SilaboRequestDTO {

    @NotNull(message = "El ID del curso es obligatorio")
    private Long cursoId;

    @NotNull(message = "El ID de la universidad es obligatorio")
    private Long universidadId;

    @NotBlank(message = "El año académico es obligatorio")
    @Pattern(regexp = "^\\d{4}$", message = "El año académico debe tener 4 dígitos (ej: 2025)")
    private String anioAcademico;

    @Min(value = 1, message = "La versión debe ser mayor a 0")
    private Integer version;

    @Pattern(regexp = "BORRADOR|EN_REVISION|APROBADO|VIGENTE|OBSOLETO", 
             message = "Estado inválido")
    private String estado;

    @DecimalMin(value = "0.00", message = "El porcentaje de calidad debe ser entre 0 y 100")
    @DecimalMax(value = "100.00", message = "El porcentaje de calidad debe ser entre 0 y 100")
    private BigDecimal porcentajeCalidad;

    private LocalDate fechaAprobacion;

    @Size(max = 200, message = "El nombre del aprobador no puede exceder 200 caracteres")
    private String aprobadoPor;

    @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
    private String observaciones;

    @NotBlank(message = "Las competencias son obligatorias")
    private String competencias;

    @NotBlank(message = "La sumilla es obligatoria")
    private String sumilla;

    private String bibliografia;

    private String metodologia;

    private String recursosDidacticos;
}
