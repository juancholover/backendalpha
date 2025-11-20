package upeu.edu.pe.catalog.application.dto;

import lombok.Data;

@Data
public class TipoLocalizacionRequestDTO {
    private String nombre; // Ej: "Aula", "Laboratorio", "Edificio"
    private String descripcion;
}
