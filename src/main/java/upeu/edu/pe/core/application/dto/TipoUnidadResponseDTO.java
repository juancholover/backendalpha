package upeu.edu.pe.core.application.dto;

import lombok.Data;

@Data
public class TipoUnidadResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer nivel;
    private Boolean active;
}
