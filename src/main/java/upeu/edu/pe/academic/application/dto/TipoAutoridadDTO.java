package upeu.edu.pe.academic.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoAutoridadDTO {
    private Long id;
    private Long universidadId;
    private String universidadNombre; // Denormalizado
    private String nombre;
    private String codigo;
    private Integer nivelJerarquia;
    private String descripcion;
}
