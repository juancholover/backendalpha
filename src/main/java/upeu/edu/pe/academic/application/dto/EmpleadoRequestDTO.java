package upeu.edu.pe.academic.application.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * DTO para crear/actualizar Empleado
 */
public record EmpleadoRequestDTO(
        
        @NotNull(message = "El ID de persona es obligatorio")
        Long personaId,
        
        Long unidadOrganizativaId,
        
        @NotBlank(message = "El código de empleado es obligatorio")
        @Size(min = 3, max = 20, message = "El código debe tener entre 3 y 20 caracteres")
        String codigoEmpleado,
        
        @NotNull(message = "La fecha de ingreso es obligatoria")
        @PastOrPresent(message = "La fecha de ingreso no puede ser futura")
        LocalDate fechaIngreso,
        
        LocalDate fechaCese,
        
        @NotBlank(message = "El cargo es obligatorio")
        @Size(max = 100, message = "El cargo no debe exceder 100 caracteres")
        String cargo,
        
        @NotBlank(message = "El tipo de contrato es obligatorio")
        @Pattern(regexp = "NOMBRADO|CONTRATADO|TEMPORAL", message = "El tipo de contrato debe ser NOMBRADO, CONTRATADO o TEMPORAL")
        String tipoContrato,
        
        @Size(max = 50, message = "El régimen laboral no debe exceder 50 caracteres")
        String regimenLaboral,
        
        @DecimalMin(value = "0.0", message = "El salario debe ser mayor o igual a 0")
        Double salario,
        
        @NotBlank(message = "El estado laboral es obligatorio")
        @Pattern(regexp = "ACTIVO|CESADO|SUSPENDIDO|LICENCIA", message = "El estado laboral debe ser ACTIVO, CESADO, SUSPENDIDO o LICENCIA")
        String estadoLaboral
) {
}
