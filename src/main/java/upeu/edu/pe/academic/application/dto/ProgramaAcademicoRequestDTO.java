package upeu.edu.pe.academic.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProgramaAcademicoRequestDTO {

    @NotNull(message = "La unidad organizativa es requerida")
    private Long unidadOrganizativaId;

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String nombre;

    @NotBlank(message = "El código es requerido")
    @Size(max = 20, message = "El código no puede exceder 20 caracteres")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "El código solo puede contener letras mayúsculas, números y guiones")
    private String codigo;

    @NotBlank(message = "El nivel académico es requerido")
    @Size(max = 50, message = "El nivel académico no puede exceder 50 caracteres")
    private String nivelAcademico; // PREGRADO, SEGUNDA_ESPECIALIDAD, MAESTRIA, DOCTORADO

    @NotBlank(message = "La modalidad es requerida")
    @Size(max = 50, message = "La modalidad no puede exceder 50 caracteres")
    private String modalidad; // PRESENCIAL, SEMIPRESENCIAL, A_DISTANCIA

    @Min(value = 1, message = "La duración en años debe ser al menos 1")
    private Integer duracionAnios;

    @NotNull(message = "La duración en semestres es requerida")
    @Min(value = 1, message = "La duración debe ser al menos 1 semestre")
    @Max(value = 20, message = "La duración no puede exceder 20 semestres")
    private Integer duracionSemestres;

    @Min(value = 0, message = "Los créditos totales no pueden ser negativos")
    private Integer creditosTotales;

    @Size(max = 200, message = "El título otorgado no puede exceder 200 caracteres")
    private String tituloOtorgado;

    @Size(max = 200, message = "El grado académico no puede exceder 200 caracteres")
    private String gradoAcademico;

    @Min(value = 1, message = "El cupo máximo debe ser al menos 1")
    private Integer cupoMaximoAnual;

    @DecimalMin(value = "0.0", message = "La nota mínima no puede ser negativa")
    @DecimalMax(value = "20.0", message = "La nota mínima no puede exceder 20")
    private BigDecimal notaMinimaIngreso;

    private Long programaPadreId;

    private LocalDate fechaCreacionPrograma;

    private LocalDate fechaUltimaModificacionPlan;

    @NotBlank(message = "El estado es requerido")
    @Size(max = 20, message = "El estado no puede exceder 20 caracteres")
    private String estado; // ACTIVO, EN_PROCESO_CIERRE, CERRADO, EN_APROBACION
}
