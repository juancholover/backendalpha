package upeu.edu.pe.security.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RolPermisoRequestDTO {

    @NotNull(message = "El ID del rol es obligatorio")
    private Long rolId;

    @NotNull(message = "El ID del permiso es obligatorio")
    private Long permisoId;

    private Boolean puedeDeleagar = false;

    @Size(max = 1000, message = "La restricción no puede exceder 1000 caracteres")
    private String restriccion;

    // Constructors
    public RolPermisoRequestDTO() {
    }

    public RolPermisoRequestDTO(Long rolId, Long permisoId, Boolean puedeDeleagar, String restriccion) {
        this.rolId = rolId;
        this.permisoId = permisoId;
        this.puedeDeleagar = puedeDeleagar;
        this.restriccion = restriccion;
    }

    // Getters and Setters
    public Long getRolId() {
        return rolId;
    }

    public void setRolId(Long rolId) {
        this.rolId = rolId;
    }

    public Long getPermisoId() {
        return permisoId;
    }

    public void setPermisoId(Long permisoId) {
        this.permisoId = permisoId;
    }

    public Boolean getPuedeDeleagar() {
        return puedeDeleagar;
    }

    public void setPuedeDeleagar(Boolean puedeDeleagar) {
        this.puedeDeleagar = puedeDeleagar;
    }

    public String getRestriccion() {
        return restriccion;
    }

    public void setRestriccion(String restriccion) {
        this.restriccion = restriccion;
    }
}
