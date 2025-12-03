package upeu.edu.pe.security.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.academic.domain.entities.Persona;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity
@Table(name = "auth_usuario", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"persona_id"}),
    @UniqueConstraint(columnNames = {"username"})
})
@EntityListeners(AuditListener.class)
public class AuthUsuario extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false, unique = true)
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidad_id", nullable = false)
    private Universidad universidad;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    @Column(name = "intentos_fallidos")
    private Integer intentosFallidos = 0;

    @Column(name = "fecha_bloqueo")
    private LocalDateTime fechaBloqueo;

    @Column(name = "requiere_cambio_password")
    private Boolean requiereCambioPassword = false;

    @Column(name = "fecha_ultimo_cambio_password")
    private LocalDateTime fechaUltimoCambioPassword;

    @Column(name = "token_recuperacion", length = 255)
    private String tokenRecuperacion;

    @Column(name = "fecha_expiracion_token")
    private LocalDateTime fechaExpiracionToken;

    // Métodos de conveniencia para obtener datos de Persona
    public String getEmail() {
        return persona != null ? persona.getEmail() : null;
    }

    public String getUsername() {
        // Usa email de persona como username
        return getEmail();
    }

    public boolean estaActivo() {
        return Boolean.TRUE.equals(this.getActive()) && fechaBloqueo == null;
    }

    public boolean estaBloqueado() {
        return fechaBloqueo != null && LocalDateTime.now().isBefore(fechaBloqueo.plusHours(24));
    }

    public void registrarAccesoExitoso() {
        this.ultimoAcceso = LocalDateTime.now();
        this.intentosFallidos = 0;
    }

    public void registrarAccesoFallido() {
        this.intentosFallidos = (this.intentosFallidos == null ? 0 : this.intentosFallidos) + 1;
        if (this.intentosFallidos >= 5) {
            this.fechaBloqueo = LocalDateTime.now();
        }
    }

    public void desbloquear() {
        this.fechaBloqueo = null;
        this.intentosFallidos = 0;
    }
}
