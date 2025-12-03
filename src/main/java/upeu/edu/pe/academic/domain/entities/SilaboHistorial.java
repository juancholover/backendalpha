package upeu.edu.pe.academic.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.annotations.Normalize;

import java.time.LocalDateTime;

/**
 * Entidad que registra el historial de cambios de un sílabo.
 * Permite auditar quién, cuándo y qué cambió en el sílabo.
 */
@Entity
@Table(name = "silabo_historial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SilaboHistorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "silabo_id", nullable = false)
    private Silabo silabo;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    @Column(name = "accion", nullable = false, length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String accion; // CREACION, MODIFICACION, ENVIO_REVISION, APROBACION, RECHAZO, ACTIVACION, OBSOLESCENCIA

    @Column(name = "usuario", nullable = false, length = 200)
    private String usuario; // Email o username del usuario que realizó la acción

    @Column(name = "comentarios", length = 1000)
    private String comentarios;

    @Column(name = "version_anterior")
    private Integer versionAnterior;

    @Column(name = "version_nueva")
    private Integer versionNueva;

    /**
     * Constructor para crear historial con datos básicos
     */
    public SilaboHistorial(Silabo silabo, String accion, String usuario, String comentarios) {
        this.silabo = silabo;
        this.accion = accion;
        this.usuario = usuario;
        this.comentarios = comentarios;
        this.fecha = LocalDateTime.now();
    }

    /**
     * Constructor para crear historial con versiones
     */
    public SilaboHistorial(Silabo silabo, String accion, String usuario, 
                           String comentarios, Integer versionAnterior, Integer versionNueva) {
        this.silabo = silabo;
        this.accion = accion;
        this.usuario = usuario;
        this.comentarios = comentarios;
        this.versionAnterior = versionAnterior;
        this.versionNueva = versionNueva;
        this.fecha = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (this.fecha == null) {
            this.fecha = LocalDateTime.now();
        }
    }
}
