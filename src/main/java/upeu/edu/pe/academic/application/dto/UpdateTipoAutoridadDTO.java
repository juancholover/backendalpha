package upeu.edu.pe.academic.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTipoAutoridadDTO {
    private String nombre;
    private Integer nivelJerarquia;
    private String descripcion;
}
