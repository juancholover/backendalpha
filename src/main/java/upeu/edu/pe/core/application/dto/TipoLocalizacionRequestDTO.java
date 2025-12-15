package upeu.edu.pe.core.application.dto;

import lombok.Data;

@Data
public class TipoLocalizacionRequestDTO {
    private String codigo; // Ej: "SEDE", "EDIFICIO", "PISO", "AULA", "LAB"
    private String nombre; // Ej: "Aula", "Laboratorio", "Edificio"
    private Long padreId; // ID del tipo padre (opcional)
    private Integer nivelJerarquia; // 1=Sede, 2=Edificio, 3=Piso, 4=Aula
    private Boolean permiteAsignacion; // TRUE si se pueden asignar actividades
}
