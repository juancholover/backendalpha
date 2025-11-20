package upeu.edu.pe.academic.application.dto;

import lombok.Data;

@Data
public class UnidadOrganizativaResponseDTO {
    private Long id;
    private Long tipoDeUnidadId;
    private String tipoDeUnidadNombre; // Denormalizado
    private Long unidadPadreId;
    private String unidadPadreNombre; // Denormalizado
    private Long localizacionId;
    private String localizacionNombre; // Denormalizado
    private String nombre;
    private String codigo;
    private String descripcion;
    private Boolean active;
}
