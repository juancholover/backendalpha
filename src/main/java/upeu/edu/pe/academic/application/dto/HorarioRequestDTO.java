package upeu.edu.pe.academic.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalTime;

@Data
public class HorarioRequestDTO {

    @NotNull(message = "El ID de la universidad es obligatorio")
    private Long universidadId;

    @NotNull(message = "El ID del curso ofertado es obligatorio")
    private Long cursoOfertadoId;

    @NotNull(message = "El día de la semana es obligatorio")
    @Min(value = 1, message = "El día debe ser entre 1 (Lunes) y 7 (Domingo)")
    @Max(value = 7, message = "El día debe ser entre 1 (Lunes) y 7 (Domingo)")
    private Integer diaSemana;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    private Long localizacionId;

    @Size(max = 20, message = "El tipo de sesión no puede exceder 20 caracteres")
    @Pattern(regexp = "TEORIA|PRACTICA|LABORATORIO|TALLER|SEMINARIO", 
             message = "El tipo de sesión debe ser TEORIA, PRACTICA, LABORATORIO, TALLER o SEMINARIO")
    private String tipoSesion;

    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    private String observaciones;
}
