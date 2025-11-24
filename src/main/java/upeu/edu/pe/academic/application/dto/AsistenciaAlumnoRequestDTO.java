package upeu.edu.pe.academic.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import lombok.Data;
import java.time.LocalDate;

@Data
public class AsistenciaAlumnoRequestDTO {
    
    @NotNull(message = "El ID de la universidad es obligatorio")
    private Long universidadId;
    
    @NotNull(message = "El ID del estudiante es obligatorio")
    private Long estudianteId;
    
    @NotNull(message = "El ID del horario es obligatorio")
    private Long horarioId;
    
    @NotNull(message = "La fecha de clase es obligatoria")
    private LocalDate fechaClase;
    
    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "PRESENTE|AUSENTE|TARDANZA|JUSTIFICADO", 
             message = "El estado debe ser: PRESENTE, AUSENTE, TARDANZA o JUSTIFICADO")
    private String estado;
    
    private String observaciones;
    
    @Min(value = 0, message = "Los minutos de tardanza no pueden ser negativos")
    private Integer minutosTardanza; // Solo si estado = TARDANZA
}
