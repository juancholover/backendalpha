package upeu.edu.pe.academic.application.dto;

import lombok.Data;

@Data
public class LocalizacionResponseDTO {
    private Long id;
    private Long tipoLocalizacionId;
    private String tipoLocalizacionNombre; // Denormalizado
    private String nombre;
    private String codigo;
    private String edificio;
    private String piso;
    private Integer capacidad;
    private String descripcion;
    private Boolean active;
}
