package upeu.edu.pe.academic.application.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class HorarioResponseDTO {

    private Long id;
    
    // Universidad
    private Long universidadId;
    private String universidadNombre;
    
    // Curso Ofertado
    private Long cursoOfertadoId;
    private String cursoOfertadoCodigoSeccion;
    private String cursoNombre; // Denormalizado
    private String cursoCodigo; // Denormalizado
    
    // Profesor
    private Long profesorId;
    private String profesorNombre; // Denormalizado
    
    // Horario
    private Integer diaSemana;
    private String nombreDia; // Lunes, Martes, etc.
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Integer duracionMinutos; // Calculado
    
    // Localización
    private Long localizacionId;
    private String localizacionNombre;
    private String localizacionCodigo;
    
    // Tipo de sesión
    private String tipoSesion;
    private String observaciones;
    
    // Auditoría
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
