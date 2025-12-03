package upeu.edu.pe.academic.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SilaboUnidadRequestDTO {

    @NotNull(message = "El ID del sílabo es obligatorio")
    private Long silaboId;

    @NotNull(message = "El número de unidad es obligatorio")
    @Min(value = 1, message = "El número de unidad debe ser mayor a 0")
    private Integer numeroUnidad;

    @NotBlank(message = "El título de la unidad es obligatorio")
    @Size(max = 200, message = "El título no puede exceder 200 caracteres")
    private String titulo;

    @NotNull(message = "La semana de inicio es obligatoria")
    @Min(value = 1, message = "La semana de inicio debe ser mayor a 0")
    @Max(value = 20, message = "La semana de inicio no puede exceder 20")
    private Integer semanaInicio;

    @NotNull(message = "La semana de fin es obligatoria")
    @Min(value = 1, message = "La semana de fin debe ser mayor a 0")
    @Max(value = 20, message = "La semana de fin no puede exceder 20")
    private Integer semanaFin;

    @NotBlank(message = "Los contenidos son obligatorios")
    private String contenidos;

    @NotBlank(message = "El logro de aprendizaje es obligatorio")
    private String logroAprendizaje;

    private String estrategiasEnsenanza;
}
