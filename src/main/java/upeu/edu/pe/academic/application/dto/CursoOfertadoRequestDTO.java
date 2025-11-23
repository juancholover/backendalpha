package upeu.edu.pe.academic.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CursoOfertadoRequestDTO {

    @NotNull(message = "La universidad es requerida")
    private Long universidadId;

    @NotNull(message = "La relación plan-curso (PlanCurso) es requerida")
    private Long planCursoId; // Referencia a PlanCurso (define curso, créditos, ciclo, tipo)

    @NotNull(message = "El período académico es requerido")
    private Long periodoAcademicoId;

    private Long profesorId; // Opcional, puede asignarse después

    @NotBlank(message = "El código de sección es requerido")
    @Size(max = 20, message = "El código de sección no puede exceder 20 caracteres")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "El código de sección solo puede contener letras mayúsculas, números y guiones")
    private String codigoSeccion; // A, B, C, 01, 02

    @NotNull(message = "La capacidad máxima es requerida")
    @Min(value = 1, message = "La capacidad máxima debe ser al menos 1")
    @Max(value = 500, message = "La capacidad máxima no puede exceder 500")
    private Integer capacidadMaxima;

    @Min(value = 0, message = "Las vacantes disponibles no pueden ser negativas")
    private Integer vacantesDisponibles;

    @NotBlank(message = "La modalidad es requerida")
    @Size(max = 50, message = "La modalidad no puede exceder 50 caracteres")
    private String modalidad; // PRESENCIAL, VIRTUAL, HIBRIDA

    private Long localizacionId; // Opcional, puede ser virtual

    @NotBlank(message = "El estado es requerido")
    @Size(max = 20, message = "El estado no puede exceder 20 caracteres")
    private String estado; // ABIERTA, CERRADA, CANCELADA, EN_CURSO, FINALIZADA

    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    private String observaciones;
}
