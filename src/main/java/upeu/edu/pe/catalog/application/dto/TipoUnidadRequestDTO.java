package upeu.edu.pe.catalog.application.dto;

import lombok.Data;

@Data
public class TipoUnidadRequestDTO {
    private String nombre; // Ej: "Facultad", "Rectorado", "Escuela"
    private String descripcion;
    private Integer nivel; // Nivel jerárquico
}
