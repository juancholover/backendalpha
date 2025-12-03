package upeu.edu.pe.academic.application.dto;

import lombok.Data;

@Data
public class TipoUnidadResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer nivel;
    private Boolean active;
}
