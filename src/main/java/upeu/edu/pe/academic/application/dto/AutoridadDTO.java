package upeu.edu.pe.academic.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutoridadDTO {
    private Long id;
    private Long universidadId;
    private Long personaId;
    private String personaNombre; // Nombre completo para mostrar
    private String personaFotoUrl;
    private Long tipoAutoridadId;
    private String tipoAutoridadNombre; // Ej: "Rector"
    private Integer nivelJerarquia;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean activo;
}
