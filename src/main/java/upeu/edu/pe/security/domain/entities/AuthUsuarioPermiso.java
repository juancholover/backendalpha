package upeu.edu.pe.security.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;

import java.time.LocalDateTime;

/**
 * Permisos individuales asignados directamente a un usuario.
 * Complementa los permisos del rol, permitiendo casos especiales sin crear roles nuevos.
 */
@Entity
@Table(name = "auth_usuario_permiso", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"auth_usuario_id", "permiso_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class AuthUsuarioPermiso extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_usuario_id", nullable = false)
    private AuthUsuario authUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permiso_id", nullable = false)
    private Permiso permiso;

    /**
     * Fecha de asignación del permiso individual
     */
    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDateTime fechaAsignacion;

    /**
     * Fecha de expiración del permiso (opcional para permisos temporales)
     */
    @Column(name = "fecha_expiracion")
    private LocalDateTime fechaExpiracion;

    /**
     * Motivo o justificación de la asignación del permiso
     */
    @Size(max = 500)
    @Column(name = "motivo", length = 500)
    private String motivo;

    /**
     * Usuario que asignó este permiso (para auditoría)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignado_por")
    private AuthUsuario asignadoPor;

    /**
     * Si es un permiso que otorga capacidad adicional (true) o restringe algo del rol (false)
     * Por defecto true = otorga permiso adicional
     */
    @Column(name = "es_adicion", nullable = false)
    private Boolean esAdicion = true;

    @PrePersist
    protected void onCreate() {
        if (fechaAsignacion == null) {
            fechaAsignacion = LocalDateTime.now();
        }
        if (esAdicion == null) {
            esAdicion = true;
        }
    }

    /**
     * Verifica si el permiso individual sigue vigente
     */
    public boolean estaVigente() {
        if (fechaExpiracion == null) {
            return true; // Sin fecha de expiración = vigencia permanente
        }
        return LocalDateTime.now().isBefore(fechaExpiracion);
    }

    /**
     * Verifica si el permiso ha expirado
     */
    public boolean estaExpirado() {
        return !estaVigente();
    }

    /**
     * Constructor para permiso permanente
     */
    public AuthUsuarioPermiso(AuthUsuario authUsuario, Permiso permiso, String motivo, AuthUsuario asignadoPor) {
        this.authUsuario = authUsuario;
        this.permiso = permiso;
        this.motivo = motivo;
        this.asignadoPor = asignadoPor;
        this.fechaAsignacion = LocalDateTime.now();
        this.esAdicion = true;
    }

    /**
     * Constructor para permiso temporal
     */
    public AuthUsuarioPermiso(AuthUsuario authUsuario, Permiso permiso, String motivo, 
                               AuthUsuario asignadoPor, LocalDateTime fechaExpiracion) {
        this.authUsuario = authUsuario;
        this.permiso = permiso;
        this.motivo = motivo;
        this.asignadoPor = asignadoPor;
        this.fechaAsignacion = LocalDateTime.now();
        this.fechaExpiracion = fechaExpiracion;
        this.esAdicion = true;
    }
}
