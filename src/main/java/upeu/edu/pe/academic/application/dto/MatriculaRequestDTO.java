package upeu.edu.pe.academic.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MatriculaRequestDTO {

    @NotNull(message = "El estudiante es requerido")
    private Long estudianteId;

    @NotNull(message = "La sección es requerida")
    private Long seccionId;

    @NotNull(message = "La fecha de matrícula es requerida")
    private LocalDate fechaMatricula;

    @NotBlank(message = "El tipo de matrícula es requerido")
    @Size(max = 50, message = "El tipo de matrícula no puede exceder 50 caracteres")
    private String tipoMatricula; // REGULAR, EXTRAORDINARIA

    @NotBlank(message = "El estado de matrícula es requerido")
    @Size(max = 20, message = "El estado de matrícula no puede exceder 20 caracteres")
    private String estadoMatricula; // MATRICULADO, RETIRADO, ANULADO

    private LocalDate fechaRetiro;

    @DecimalMin(value = "0.00", message = "La nota final no puede ser negativa")
    @DecimalMax(value = "20.00", message = "La nota final no puede exceder 20")
    private BigDecimal notaFinal;

    @Size(max = 20, message = "El estado de aprobación no puede exceder 20 caracteres")
    private String estadoAprobacion; // APROBADO, DESAPROBADO, RETIRADO, PENDIENTE

    @Min(value = 0, message = "Las inasistencias no pueden ser negativas")
    private Integer inasistencias;
}
