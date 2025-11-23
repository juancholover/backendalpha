package upeu.edu.pe.academic.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PlanCursoRequestDTO {

    @NotNull(message = "La universidad es requerida")
    private Long universidadId;

    @NotNull(message = "El plan académico es requerido")
    private Long planAcademicoId;

    @NotNull(message = "El curso es requerido")
    private Long cursoId;

    @NotNull(message = "Los créditos son requeridos")
    @Min(value = 1, message = "Los créditos deben ser mayor a 0")
    @Max(value = 20, message = "Los créditos no pueden exceder 20")
    private Integer creditos;

    @NotNull(message = "El ciclo es requerido")
    @Min(value = 1, message = "El ciclo debe ser mayor a 0")
    @Max(value = 20, message = "El ciclo no puede exceder 20")
    private Integer ciclo;

    @NotBlank(message = "El tipo de curso es requerido")
    @Pattern(regexp = "^(OBLIGATORIO|ELECTIVO)$", message = "El tipo debe ser OBLIGATORIO o ELECTIVO")
    private String tipoCurso;

    private Boolean esObligatorio;
}
