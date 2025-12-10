package upeu.edu.pe.shared.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad para registrar todas las acciones de auditoría del sistema.
 * Almacena quién, qué, cuándo y desde dónde se realizaron cambios en el sistema.
 */
@Entity
@Table(
    name = "auditoria_general",
    indexes = {
        @Index(name = "idx_auditoria_usuario", columnList = "usuario"),
        @Index(name = "idx_auditoria_tabla", columnList = "tabla"),
        @Index(name = "idx_auditoria_fecha", columnList = "fecha_hora"),
        @Index(name = "idx_auditoria_modulo", columnList = "modulo"),
        @Index(name = "idx_auditoria_accion", columnList = "accion")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditoriaGeneral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Long idAuditoria;

    /**
     * Fecha y hora de la acción
     */
    @NotNull(message = "La fecha y hora es obligatoria")
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    /**
     * Usuario que realizó la acción (username como "admin_ext", "rector_principal")
     */
    @NotBlank(message = "El usuario es obligatorio")
    @Size(max = 100)
    @Column(name = "usuario", nullable = false, length = 100)
    private String usuario;

    /**
     * Tipo de acción: SELECT, INSERT, UPDATE, DELETE
     */
    @NotBlank(message = "La acción es obligatoria")
    @Size(max = 50)
    @Column(name = "accion", nullable = false, length = 50)
    private String accion;

    /**
     * Tabla afectada (ej: "AuthUsuario", "Universidad")
     */
    @NotBlank(message = "La tabla es obligatoria")
    @Size(max = 100)
    @Column(name = "tabla", nullable = false, length = 100)
    private String tabla;

    /**
     * Módulo del sistema (ej: "Seguridad", "Configuración Global")
     */
    @NotBlank(message = "El módulo es obligatorio")
    @Size(max = 100)
    @Column(name = "modulo", nullable = false, length = 100)
    private String modulo;

    /**
     * Dirección IP del usuario
     */
    @Column(name = "ip_address", columnDefinition = "inet")
    private String ipAddress;

    /**
     * Valores antes del cambio (formato JSONB)
     */
    @Column(name = "datos_anteriores", columnDefinition = "jsonb")
    private String datosAnteriores;

    /**
     * Valores después del cambio (formato JSONB)
     */
    @Column(name = "datos_nuevos", columnDefinition = "jsonb")
    private String datosNuevos;

    /**
     * Observaciones adicionales
     */
    @Column(name = "observaciones", columnDefinition = "text")
    private String observaciones;

    /**
     * Hook pre-persistencia para establecer fecha automática
     */
    @PrePersist
    protected void onCreate() {
        if (fechaHora == null) {
            fechaHora = LocalDateTime.now();
        }
    }
}
