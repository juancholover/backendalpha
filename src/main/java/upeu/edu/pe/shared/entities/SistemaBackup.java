package upeu.edu.pe.shared.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.listeners.AuditListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity para gestión de backups del sistema (PostgreSQL, aplicación).
 * RF232: Sistema de Backups y Logs - Funcionalidad externa a BD.
 */
@Entity
@Table(name = "sistema_backup",
    indexes = {
        @Index(name = "idx_backup_fecha", columnList = "fecha_hora"),
        @Index(name = "idx_backup_tipo_estado", columnList = "tipo, estado"),
        @Index(name = "idx_backup_usuario", columnList = "usuario_solicitante")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class SistemaBackup extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long idBackup;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    /**
     * Tipo de backup: AUTOMATICO, MANUAL
     */
    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo; // AUTOMATICO, MANUAL

    /**
     * Estado del backup: EXITOSO, ERROR
     */
    @Column(name = "estado", nullable = false, length = 20)
    private String estado; // EXITOSO, ERROR

    /**
     * Tamaño del backup en GB
     */
    @Column(name = "tamano_gb", precision = 8, scale = 2)
    private BigDecimal tamanoGb;

    /**
     * Duración del backup en minutos
     */
    @Column(name = "duracion_minuto")
    private Integer duracionMinuto;

    /**
     * Número de registros procesados (estimado)
     */
    @Column(name = "registros_procesados")
    private Integer registrosProcesados;

    /**
     * Ruta del archivo de backup (S3, MinIO, filesystem local)
     */
    @Column(name = "archivo_destino", length = 500)
    private String archivoDestino;

    /**
     * Usuario que solicitó el backup (solo para MANUAL)
     */
    @Column(name = "usuario_solicitante", length = 100)
    private String usuarioSolicitante;

    /**
     * Observaciones adicionales (errores, advertencias, etc.)
     */
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    /**
     * Indica si el backup fue exitoso
     */
    public boolean esExitoso() {
        return "EXITOSO".equals(this.estado);
    }

    /**
     * Indica si el backup fue manual
     */
    public boolean esManual() {
        return "MANUAL".equals(this.tipo);
    }
}
