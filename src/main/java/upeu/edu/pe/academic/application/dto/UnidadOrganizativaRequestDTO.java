package upeu.edu.pe.academic.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UnidadOrganizativaRequestDTO {
    
    @NotNull(message = "El ID de la universidad es obligatorio")
    private Long universidadId;
    
    private Long localizacionId; // Opcional: ubicación física de la unidad
    
    @NotNull(message = "El tipo de unidad es obligatorio")
    private Long tipoUnidadId; // FK a TipoUnidad (FACULTAD, ESCUELA, etc.)
    
    private Long unidadPadreId; // Opcional: para jerarquía organizacional
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String nombre;
    
    @Size(max = 20, message = "El código no puede exceder 20 caracteres")
    private String codigo; // Ej: FAC-ING, ESC-SIS
    
    @Size(max = 20, message = "La sigla no puede exceder 20 caracteres")
    private String sigla; // Ej: FI, ESIS
    
    private String descripcion;
}
