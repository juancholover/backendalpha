package upeu.edu.pe.academic.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAutoridadDTO {
    private Long personaId;
    private Long tipoAutoridadId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean activo;
    private String resolucionDesignacion;
    private String observaciones;
}
