package upeu.edu.pe.academic.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class EstudianteRequestDTO {

    @NotNull(message = "El ID de la persona es obligatorio")
    private Long personaId;

    @NotNull(message = "El ID del programa académico es obligatorio")
    private Long programaAcademicoId;

    @NotBlank(message = "El código de estudiante es obligatorio")
    @Size(max = 20, message = "El código de estudiante no puede exceder 20 caracteres")
    private String codigoEstudiante;

    @NotNull(message = "La fecha de ingreso es obligatoria")
    @PastOrPresent(message = "La fecha de ingreso no puede ser futura")
    private LocalDate fechaIngreso;

    @NotNull(message = "El ciclo actual es obligatorio")
    @Min(value = 1, message = "El ciclo actual debe ser mayor a 0")
    @Max(value = 20, message = "El ciclo actual no puede exceder 20")
    private Integer cicloActual;

    @Min(value = 0, message = "Los créditos aprobados no pueden ser negativos")
    private Integer creditosAprobados;

    @DecimalMin(value = "0.00", message = "El promedio no puede ser negativo")
    @DecimalMax(value = "20.00", message = "El promedio no puede exceder 20")
    @Digits(integer = 2, fraction = 2, message = "El promedio debe tener máximo 2 enteros y 2 decimales")
    private java.math.BigDecimal promedioPonderado;

    @Size(max = 50, message = "La modalidad de ingreso no puede exceder 50 caracteres")
    private String modalidadIngreso;

    @Pattern(regexp = "ACTIVO|RETIRADO|EGRESADO|GRADUADO|LICENCIA", message = "Estado académico inválido")
    private String estadoAcademico;

    @Pattern(regexp = "REGULAR|IRREGULAR", message = "Tipo de estudiante inválido")
    private String tipoEstudiante;
}
