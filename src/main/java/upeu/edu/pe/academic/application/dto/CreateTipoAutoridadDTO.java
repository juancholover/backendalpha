package upeu.edu.pe.academic.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTipoAutoridadDTO {
    private Long universidadId;
    private String nombre;
    private String codigo; // Código único para consultas (RECTOR, DECANO, etc.)
    private Integer nivelJerarquia;
    private String descripcion;
}
