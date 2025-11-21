package upeu.edu.pe.academic.application.dto;

import lombok.Data;

@Data
public class UnidadOrganizativaRequestDTO {
    private Long tipoDeUnidadId; // FK a tipo_de_unidad
    private Long unidadPadreId; // FK a unidad_organizativa (jerarquía)
    private Long localizacionId; // FK a localizacion
    private String nombre; 
    private String codigo;
    private String sigla;
    private String descripcion;
}
