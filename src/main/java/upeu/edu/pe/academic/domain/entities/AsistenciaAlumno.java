package upeu.edu.pe.academic.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;
import upeu.edu.pe.shared.annotations.Normalize;

import java.time.LocalDate;


@Entity
@Table(name = "asistencia_alumno", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"estudiante_id", "horario_id", "fecha_clase"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class AsistenciaAlumno extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidad_id", nullable = false)
    private Universidad universidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horario_id", nullable = false)
    private Horario horario;

    @Column(name = "fecha_clase", nullable = false)
    private LocalDate fechaClase;

    @Column(name = "estado", nullable = false, length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String estado; // PRESENTE, AUSENTE, TARDANZA, JUSTIFICADO

    @Column(name = "observaciones", columnDefinition = "TEXT")
    @Normalize(Normalize.NormalizeType.SPACES_ONLY)
    private String observaciones;

    @Column(name = "minutos_tardanza")
    private Integer minutosTardanza; // Solo si estado = TARDANZA

    /**
     * Validar que el estado sea válido
     */
    @PrePersist
    @PreUpdate
    private void validarEstado() {
        if (estado != null && 
            !estado.equals("PRESENTE") && 
            !estado.equals("AUSENTE") && 
            !estado.equals("TARDANZA") && 
            !estado.equals("JUSTIFICADO")) {
            throw new IllegalArgumentException("Estado de asistencia inválido: " + estado);
        }
    }

    /**
     * Verificar si el estudiante asistió
     */
    public boolean asistio() {
        return "PRESENTE".equals(estado) || "TARDANZA".equals(estado);
    }

    /**
     * Verificar si necesita justificación
     */
    public boolean necesitaJustificacion() {
        return "AUSENTE".equals(estado) && !"JUSTIFICADO".equals(estado);
    }
}
