package upeu.edu.pe.academic.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SilaboActividadRequestDTO {

    @NotNull(message = "El ID de la unidad es obligatorio")
    private Long unidadId;

    @NotBlank(message = "El tipo de actividad es obligatorio")
    @Pattern(regexp = "FORMATIVA|SUMATIVA", message = "El tipo debe ser FORMATIVA o SUMATIVA")
    private String tipo;

    @NotBlank(message = "El nombre de la actividad es obligatorio")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String nombre;

    private String descripcion;

    @DecimalMin(value = "0.00", message = "La ponderación debe ser entre 0 y 100")
    @DecimalMax(value = "100.00", message = "La ponderación debe ser entre 0 y 100")
    private BigDecimal ponderacion;

    @Min(value = 1, message = "La semana programada debe ser mayor a 0")
    @Max(value = 20, message = "La semana programada no puede exceder 20")
    private Integer semanaProgramada;

    @Size(max = 100, message = "El instrumento de evaluación no puede exceder 100 caracteres")
    private String instrumentoEvaluacion;

    private String indicadores;

    private String criteriosEvaluacion;
}
