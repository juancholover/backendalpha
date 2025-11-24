package upeu.edu.pe.academic.application.dto;

import lombok.Data;

@Data
public class UnidadOrganizativaResponseDTO {
    
    private Long id;
    private Long universidadId;
    private String universidadNombre; // Denormalizado
    
    private Long localizacionId;
    private String localizacionNombre; // Denormalizado
    
    private Long tipoUnidadId;
    private String tipoUnidadNombre; // Denormalizado (FACULTAD, ESCUELA, etc.)
    private Integer tipoUnidadNivel; // Denormalizado (1=Facultad, 2=Escuela, etc.)
    
    private Long unidadPadreId;
    private String unidadPadreNombre; // Denormalizado
    
    private String nombre;
    private String codigo;
    private String sigla;
    private String descripcion;
    
    private Boolean active; // De AuditableEntity
}
