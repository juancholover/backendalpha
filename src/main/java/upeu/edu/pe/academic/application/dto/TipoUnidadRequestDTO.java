package upeu.edu.pe.academic.application.dto;

import lombok.Data;

@Data
public class TipoUnidadRequestDTO {
    private String nombre; // Ej: "Facultad", "Rectorado", "Escuela"
    private String descripcion;
    private Integer nivel; // Nivel jerárquico
}
