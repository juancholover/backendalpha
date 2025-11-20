package upeu.edu.pe.security.application.dto;

import java.time.LocalDateTime;

public class RolPermisoResponseDTO {

    private Long id;
    private Long rolId;
    private String rolNombre;
    private Long permisoId;
    private String permisoNombreClave;
    private String permisoModulo;
    private String permisoRecurso;
    private String permisoAccion;
    private Boolean puedeDeleagar;
    private String restriccion;
    private LocalDateTime createdAt;
    private String createdBy;

    // Constructors
    public RolPermisoResponseDTO() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRolId() {
        return rolId;
    }

    public void setRolId(Long rolId) {
        this.rolId = rolId;
    }

    public String getRolNombre() {
        return rolNombre;
    }

    public void setRolNombre(String rolNombre) {
        this.rolNombre = rolNombre;
    }

    public Long getPermisoId() {
        return permisoId;
    }

    public void setPermisoId(Long permisoId) {
        this.permisoId = permisoId;
    }

    public String getPermisoNombreClave() {
        return permisoNombreClave;
    }

    public void setPermisoNombreClave(String permisoNombreClave) {
        this.permisoNombreClave = permisoNombreClave;
    }

    public String getPermisoModulo() {
        return permisoModulo;
    }

    public void setPermisoModulo(String permisoModulo) {
        this.permisoModulo = permisoModulo;
    }

    public String getPermisoRecurso() {
        return permisoRecurso;
    }

    public void setPermisoRecurso(String permisoRecurso) {
        this.permisoRecurso = permisoRecurso;
    }

    public String getPermisoAccion() {
        return permisoAccion;
    }

    public void setPermisoAccion(String permisoAccion) {
        this.permisoAccion = permisoAccion;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
