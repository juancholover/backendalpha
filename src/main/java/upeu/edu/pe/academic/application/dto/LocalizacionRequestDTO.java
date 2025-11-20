package upeu.edu.pe.academic.application.dto;

import lombok.Data;

@Data
public class LocalizacionRequestDTO {
    private Long tipoLocalizacionId; // FK a Tipo_de_localizacion
    private String nombre; // Nombre del lugar
    private String codigo; // Código único
    private String edificio;
    private String piso;
    private Integer capacidad;
    private String descripcion;
}
