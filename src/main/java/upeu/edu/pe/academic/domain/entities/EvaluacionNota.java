package upeu.edu.pe.academic.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluacion_nota", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"matricula_id", "criterio_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class EvaluacionNota extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula; // Estudiante matriculado en la sección

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criterio_id", nullable = false)
    private EvaluacionCriterio criterio; // Criterio de evaluación

    @Column(name = "nota", precision = 5, scale = 2)
    private BigDecimal nota; // Nota obtenida (puede ser null si aún no está calificado)

    @Column(name = "nota_recuperacion", precision = 5, scale = 2)
    private BigDecimal notaRecuperacion; // Nota de recuperación (si aplica)

    @Column(name = "nota_final", precision = 5, scale = 2)
    private BigDecimal notaFinal; // La mayor entre nota y notaRecuperacion

    @Column(name = "observacion", length = 500)
    private String observacion; // Comentarios del profesor

    @Column(name = "fecha_evaluacion")
    private LocalDateTime fechaEvaluacion; // Cuándo se realizó la evaluación

    @Column(name = "fecha_calificacion")
    private LocalDateTime fechaCalificacion; // Cuándo se registró la nota

    @Column(name = "estado", length = 20)
    private String estado = "PENDIENTE"; // PENDIENTE, CALIFICADO, AUSENTE, NSP (No Se Presentó)

   
    public EvaluacionNota(Matricula matricula, EvaluacionCriterio criterio) {
        this.matricula = matricula;
        this.criterio = criterio;
        this.estado = "PENDIENTE";
    }


    public EvaluacionNota(Matricula matricula, EvaluacionCriterio criterio, BigDecimal nota) {
        this.matricula = matricula;
        this.criterio = criterio;
        this.nota = nota;
        this.notaFinal = nota;
        this.estado = "CALIFICADO";
        this.fechaCalificacion = LocalDateTime.now();
    }

    /**
     * Calcula la nota final (la mayor entre nota normal y recuperación)
     */
    private void calcularNotaFinal() {
        if (nota != null && notaRecuperacion != null) {
            this.notaFinal = nota.compareTo(notaRecuperacion) > 0 ? nota : notaRecuperacion;
        } else if (nota != null) {
            this.notaFinal = nota;
        } else if (notaRecuperacion != null) {
            this.notaFinal = notaRecuperacion;
        }
    }

    /**
     * Hook que se ejecuta automáticamente antes de INSERT y UPDATE
     * para garantizar que notaFinal siempre esté sincronizada
     */
    @PrePersist
    @PreUpdate
    public void calcularNotaFinalAutomatico() {
        calcularNotaFinal();
    }


    public void registrarNotaRecuperacion(BigDecimal notaRecuperacion) {
        this.notaRecuperacion = notaRecuperacion;
        calcularNotaFinal();
        if (this.estado == null || "PENDIENTE".equals(this.estado)) {
            this.estado = "CALIFICADO";
            this.fechaCalificacion = LocalDateTime.now();
        }
    }

    /**
     * Verifica si el estudiante aprobó esta evaluación
     */
    public boolean estaAprobado() {
        if (notaFinal == null || criterio == null || criterio.getNotaMinimaAprobatoria() == null) {
            return false;
        }
        return notaFinal.compareTo(new BigDecimal(criterio.getNotaMinimaAprobatoria())) >= 0;
    }
}
