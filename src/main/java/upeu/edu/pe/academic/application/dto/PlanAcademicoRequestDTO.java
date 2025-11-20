package upeu.edu.pe.academic.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PlanAcademicoRequestDTO {

    @NotNull(message = "El programa académico es requerido")
    private Long programaAcademicoId;

    @NotBlank(message = "El código es requerido")
    @Size(max = 20, message = "El código no puede exceder 20 caracteres")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "El código solo puede contener letras mayúsculas, números y guiones")
    private String codigo;

    @NotBlank(message = "La versión es requerida")
    @Size(max = 10, message = "La versión no puede exceder 10 caracteres")
    private String version; // 2020, 2021, 2022, etc.

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String nombre;

    @Size(max = 100, message = "La resolución de aprobación no puede exceder 100 caracteres")
    private String resolucionAprobacion;

    private LocalDate fechaAprobacion;

    @NotNull(message = "La fecha de inicio de vigencia es requerida")
    private LocalDate fechaVigenciaInicio;

    private LocalDate fechaVigenciaFin;

    @Min(value = 0, message = "Los créditos totales no pueden ser negativos")
    private Integer creditosTotales;

    @NotBlank(message = "El estado es requerido")
    @Size(max = 20, message = "El estado no puede exceder 20 caracteres")
    private String estado; // VIGENTE, OBSOLETO, EN_PROCESO
}
