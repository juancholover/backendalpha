package upeu.edu.pe.security.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.academic.domain.entities.Persona;
import upeu.edu.pe.security.domain.enums.UserRole;
import upeu.edu.pe.security.domain.enums.UserStatus;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;
import upeu.edu.pe.shared.annotations.Normalize;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class User extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    @Normalize(Normalize.NormalizeType.LOWERCASE) // usernames en minúsculas
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    @Normalize(Normalize.NormalizeType.LOWERCASE) // emails en minúsculas
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 50)
    @Normalize(Normalize.NormalizeType.TITLE_CASE) // Primera letra mayúscula
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    @Normalize(Normalize.NormalizeType.TITLE_CASE) // Primera letra mayúscula
    private String lastName;

    @Column(length = 15)
    @Normalize(Normalize.NormalizeType.SPACES_ONLY) // Solo limpiar espacios
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    // Relación opcional con Persona (para integración con sistema académico)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", unique = true)
    private Persona persona;

    @Column(name = "intentos_fallidos")
    private Integer intentosFallidos = 0;

    @Column(name = "fecha_bloqueo")
    private LocalDateTime fechaBloqueo;

    @Column(name = "token_recuperacion", length = 255)
    private String tokenRecuperacion;

    @Column(name = "fecha_expiracion_token")
    private LocalDateTime fechaExpiracionToken;
}