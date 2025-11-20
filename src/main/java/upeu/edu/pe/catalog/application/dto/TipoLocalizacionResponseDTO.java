package upeu.edu.pe.catalog.application.dto;

import lombok.Data;

@Data
public class TipoLocalizacionResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Boolean active;
}
