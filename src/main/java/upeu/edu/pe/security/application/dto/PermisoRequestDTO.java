package upeu.edu.pe.security.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PermisoRequestDTO {

    @NotBlank(message = "El módulo es obligatorio")
    @Size(max = 50, message = "El módulo no puede exceder 50 caracteres")
    private String modulo;

    @NotBlank(message = "El recurso es obligatorio")
    @Size(max = 50, message = "El recurso no puede exceder 50 caracteres")
    private String recurso;

    @NotBlank(message = "La acción es obligatoria")
    @Size(max = 50, message = "La acción no puede exceder 50 caracteres")
    private String accion;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    @Size(max = 100, message = "El nombre clave no puede exceder 100 caracteres")
    private String nombreClave;

    // Constructors
    public PermisoRequestDTO() {
    }

    public PermisoRequestDTO(String nombreClave, String modulo, String recurso, String accion, String descripcion) {
        this.nombreClave = nombreClave;
        this.modulo = modulo;
        this.recurso = recurso;
        this.accion = accion;
        this.descripcion = descripcion;
    }

    // Getters and Setters
    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getRecurso() {
        return recurso;
    }

    public void setRecurso(String recurso) {
        this.recurso = recurso;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombreClave() {
        return nombreClave;
    }

    public void setNombreClave(String nombreClave) {
        this.nombreClave = nombreClave;
    }
}
