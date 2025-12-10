package upeu.edu.pe.core.application.dto;

import lombok.Data;

@Data
public class TipoLocalizacionRequestDTO {
    private String nombre; // Ej: "Aula", "Laboratorio", "Edificio"
    private String descripcion;
}
