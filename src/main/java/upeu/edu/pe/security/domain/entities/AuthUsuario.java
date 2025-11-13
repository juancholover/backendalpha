package upeu.edu.pe.security.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import upeu.edu.pe.academic.domain.entities.Persona;
import upeu.edu.pe.shared.entities.AuditableEntity;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "auth_usuario", uniqueConstraints = {
    @UniqueConstraint(columnNames = "username"),
    @UniqueConstraint(columnNames = "email"),
    @UniqueConstraint(columnNames = "persona_id")
})
public class AuthUsuario extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(length = 50)
    private String rol; // ESTUDIANTE, PROFESOR, ADMIN, EMPLEADO

    @Column(length = 20)
    private String estado; // ACTIVO, BLOQUEADO, SUSPENDIDO

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    @Column(name = "intentos_fallidos")
    private Integer intentosFallidos = 0;

    @Column(name = "fecha_bloqueo")
    private LocalDateTime fechaBloqueo;

    @Column(name = "token_recuperacion", length = 255)
    private String tokenRecuperacion;

    @Column(name = "fecha_expiracion_token")
    private LocalDateTime fechaExpiracionToken;

    @Column(name = "requiere_cambio_password")
    private Boolean requiereCambioPassword = false;

    @Column(name = "fecha_ultimo_cambio_password")
    private LocalDateTime fechaUltimoCambioPassword;

    @PrePersist
    public void prePersist() {
        if (this.intentosFallidos == null) {
            this.intentosFallidos = 0;
        }
        if (this.requiereCambioPassword == null) {
            this.requiereCambioPassword = false;
        }
        if (this.estado == null) {
            this.estado = "ACTIVO";
        }
    }
}
