package upeu.edu.pe.academic.application.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProgramaAcademicoResponseDTO {

    private Long id;

    // Información de la unidad organizativa
    private Long unidadOrganizativaId;
    private String unidadOrganizativaNombre;
    private String unidadOrganizativaCodigo;

    // Información del programa
    private String nombre;
    private String codigo;
    private String nivelAcademico;
    private String modalidad;
    private Integer duracionAnios;
    private Integer duracionSemestres;
    private Integer creditosTotales;
    private String tituloOtorgado;
    private String gradoAcademico;
    private Integer cupoMaximoAnual;
    private BigDecimal notaMinimaIngreso;
    private Long programaPadreId;
    private LocalDate fechaCreacionPrograma;
    private LocalDate fechaUltimaModificacionPlan;
    private String estado;

    // Auditoría
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
