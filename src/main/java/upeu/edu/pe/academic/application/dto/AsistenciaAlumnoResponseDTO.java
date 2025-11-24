package upeu.edu.pe.academic.application.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AsistenciaAlumnoResponseDTO {
    
    private Long id;
    private Long universidadId;
    
    private Long estudianteId;
    private String estudianteNombre; // Denormalizado
    private String estudianteCodigo; // Denormalizado
    
    private Long horarioId;
    private String cursoNombre; // Denormalizado desde horario
    private String cursoCodigo; // Denormalizado desde horario
    private Integer diaSemana; // Denormalizado desde horario
    private String nombreDia; // Denormalizado desde horario
    private LocalTime horaInicio; // Denormalizado desde horario
    private LocalTime horaFin; // Denormalizado desde horario
    private String localizacionNombre; // Denormalizado desde horario
    
    private LocalDate fechaClase;
    private String estado; // PRESENTE, AUSENTE, TARDANZA, JUSTIFICADO
    private String observaciones;
    private Integer minutosTardanza;
    
    private Boolean active; // De AuditableEntity
}
