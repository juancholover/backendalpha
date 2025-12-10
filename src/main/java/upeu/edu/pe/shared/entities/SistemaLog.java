package upeu.edu.pe.shared.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.listeners.AuditListener;

import java.time.LocalDateTime;

/**
 * Entity para gestión de logs del sistema (eventos, errores, auditoría).
 * RF232: Sistema de Backups y Logs - Funcionalidad externa a BD.
 * 
 * Niveles: INFO, WARNING, ERROR, CRITICAL
 * Módulos: AUTH, SEGURIDAD, INTEGRACION, BACKUP, DATABASE, MANTENIMIENTO, SISTEMA, etc.
 */
@Entity
@Table(name = "sistema_log",
    indexes = {
        @Index(name = "idx_log_fecha", columnList = "fecha_hora"),
        @Index(name = "idx_log_nivel", columnList = "nivel"),
        @Index(name = "idx_log_modulo", columnList = "modulo"),
        @Index(name = "idx_log_usuario", columnList = "usuario"),
        @Index(name = "idx_log_ip", columnList = "ip_origen")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class SistemaLog extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long idLog;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    /**
     * Nivel de log: INFO, WARNING, ERROR, CRITICAL
     */
    @Column(name = "nivel", nullable = false, length = 20)
    private String nivel;

    /**
     * Módulo del sistema que generó el log
     * AUTH, SEGURIDAD, INTEGRACION, BACKUP, DATABASE, MANTENIMIENTO, SISTEMA, CURRICULUM, MATRICULA, FINANCIERO
     */
    @Column(name = "modulo", nullable = false, length = 100)
    private String modulo;

    /**
     * Mensaje descriptivo del evento
     */
    @Column(name = "mensaje", columnDefinition = "TEXT", nullable = false)
    private String mensaje;

    /**
     * Usuario que ejecutó la acción (si aplica)
     */
    @Column(name = "usuario", length = 100)
    private String usuario;

    /**
     * Información adicional en formato JSONB (contexto, parámetros, stack trace)
     */
    @Column(name = "contexto", columnDefinition = "JSONB")
    private String contexto;

    /**
     * Dirección IP de origen (INET)
     */
    @Column(name = "ip_origen", length = 50)
    private String ipOrigen;

    /**
     * Indica si es un error crítico
     */
    public boolean esCritico() {
        return "CRITICAL".equals(this.nivel);
    }

    /**
     * Indica si es un error
     */
    public boolean esError() {
        return "ERROR".equals(this.nivel) || "CRITICAL".equals(this.nivel);
    }

    /**
     * Indica si es una advertencia
     */
    public boolean esWarning() {
        return "WARNING".equals(this.nivel);
    }

    /**
     * Indica si es informativo
     */
    public boolean esInfo() {
        return "INFO".equals(this.nivel);
    }
}
