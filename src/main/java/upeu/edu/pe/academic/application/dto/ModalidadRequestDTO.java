package upeu.edu.pe.academic.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ModalidadRequestDTO {

    @NotNull(message = "El ID de la universidad es obligatorio")
    private Long universidadId;

    @NotBlank(message = "El código de la modalidad es obligatorio")
    @Size(max = 20, message = "El código no puede exceder 20 caracteres")
    @Pattern(regexp = "PRES|VIRT|SEMI|HIBR|[A-Z]{3,5}", message = "Código debe ser mayúsculas (ej: PRES, VIRT, SEMI, HIBR)")
    private String codigo;

    @NotBlank(message = "El nombre de la modalidad es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    @NotNull(message = "Debe indicar si requiere aula física")
    private Boolean requiereAula;

    @NotNull(message = "Debe indicar si requiere plataforma digital")
    private Boolean requierePlataforma;

    @Min(value = 0, message = "El porcentaje de presencialidad debe ser entre 0 y 100")
    @Max(value = 100, message = "El porcentaje de presencialidad debe ser entre 0 y 100")
    private Integer porcentajePresencialidad;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "El color debe estar en formato hexadecimal (#RRGGBB)")
    private String colorHex;
}
