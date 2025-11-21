package upeu.edu.pe.academic.application.dto;

import lombok.Data;

@Data
public class UnidadOrganizativaResponseDTO {
    private Long id;
    private Long tipoDeUnidadId;
    private String tipoDeUnidadNombre; 
    private Long unidadPadreId;
    private String unidadPadreNombre; 
    private Long localizacionId;
    private String localizacionNombre;
    private String nombre;
    private String codigo;
    private String sigla;
    private String descripcion;
    private Boolean active;
}
