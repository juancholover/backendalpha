package upeu.edu.pe.security.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolPermisoResponseDTO {

    private Long id;
    private Long rolId;
    private String rolNombre;
    private Long permisoId;
    private String permisoNombreClave;
    private String permisoModulo;
    private String permisoRecurso;
    private String permisoAccion;
    private Boolean puedeDelegar;
    private String restriccion;
    private LocalDateTime createdAt;
    private String createdBy;
}
