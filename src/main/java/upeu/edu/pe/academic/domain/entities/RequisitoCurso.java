package upeu.edu.pe.academic.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;
import upeu.edu.pe.shared.annotations.Normalize;

@Entity
@Table(name = "requisito_curso", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"curso_id", "curso_requisito_id", "tipo_requisito", "universidad_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class RequisitoCurso extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidad_id", nullable = false)
    private Universidad universidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso; // Curso destino (ej: Cálculo II)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_requisito_id", nullable = false)
    private Curso cursoRequisito; // Curso fuente (ej: Cálculo I)

    @Column(name = "tipo_requisito", nullable = false, length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String tipoRequisito; // PRERREQUISITO, CORREQUISITO

    @Column(name = "es_obligatorio")
    private Boolean esObligatorio = true; // Si es requisito estricto o sugerido

    @Column(name = "nota_minima_requerida")
    private Integer notaMinimaRequerida; // Nota mínima para considerar aprobado el requisito

    @Column(name = "observacion", length = 500)
    @Normalize(Normalize.NormalizeType.SPACES_ONLY)
    private String observacion; // Ej: "Se puede llevar en paralelo con autorización"

    /**
     * Constructor de conveniencia para prerrequisito simple
     */
    public RequisitoCurso(Universidad universidad, Curso curso, Curso cursoRequisito, String tipoRequisito) {
        this.universidad = universidad;
        this.curso = curso;
        this.cursoRequisito = cursoRequisito;
        this.tipoRequisito = tipoRequisito;
        this.esObligatorio = true;
    }

    /**
     * Constructor completo
     */
    public RequisitoCurso(Universidad universidad, Curso curso, Curso cursoRequisito, String tipoRequisito, 
                         Boolean esObligatorio, Integer notaMinimaRequerida) {
        this.universidad = universidad;
        this.curso = curso;
        this.cursoRequisito = cursoRequisito;
        this.tipoRequisito = tipoRequisito;
        this.esObligatorio = esObligatorio;
        this.notaMinimaRequerida = notaMinimaRequerida;
    }

    @PrePersist
    public void prePersist() {
        if (this.esObligatorio == null) {
            this.esObligatorio = true;
        }
        if (this.tipoRequisito == null) {
            this.tipoRequisito = "PRERREQUISITO";
        }
        validarRequisito();
    }

    @PreUpdate
    public void preUpdate() {
        validarRequisito();
    }

    /**
     * Validación: un curso no puede ser prerequisito de sí mismo
     */
    private void validarRequisito() {
        if (curso != null && cursoRequisito != null && curso.getId().equals(cursoRequisito.getId())) {
            throw new IllegalArgumentException("Un curso no puede ser requisito de sí mismo");
        }
    }
}
