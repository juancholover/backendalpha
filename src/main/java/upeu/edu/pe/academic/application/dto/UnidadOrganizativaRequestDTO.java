package upeu.edu.pe.academic.application.dto;

import lombok.Data;

@Data
public class UnidadOrganizativaRequestDTO {
    private Long tipoDeUnidadId; // FK a tipo_de_unidad
    private Long unidadPadreId; // FK a unidad_organizativa (jerarquía)
    private Long localizacionId; // FK a localizacion
    private String nombre; // Nombre de la unidad
    private String codigo; // Código único
    private String descripcion;
}
