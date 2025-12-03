package upeu.edu.pe.academic.application.dto;

import lombok.Data;

@Data
public class TipoLocalizacionRequestDTO {
    private String nombre; // Ej: "Aula", "Laboratorio", "Edificio"
    private String descripcion;
}
