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
@Table(name = "plan_curso", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"plan_academico_id", "curso_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class PlanCurso extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidad_id", nullable = false)
    private Universidad universidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_academico_id", nullable = false)
    private PlanAcademico planAcademico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @Column(name = "creditos", nullable = false)
    private Integer creditos; // Créditos del curso en este plan

    @Column(name = "ciclo", nullable = false)
    private Integer ciclo; // Ciclo donde se dicta el curso (1, 2, 3, etc.)

    @Column(name = "tipo_curso", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String tipoCurso; // OBLIGATORIO, ELECTIVO

    @Column(name = "es_obligatorio")
    private Boolean esObligatorio = true;

    
    public PlanCurso(Universidad universidad, PlanAcademico planAcademico, Curso curso, 
                     Integer creditos, Integer ciclo, String tipoCurso) {
        this.universidad = universidad;
        this.planAcademico = planAcademico;
        this.curso = curso;
        this.creditos = creditos;
        this.ciclo = ciclo;
        this.tipoCurso = tipoCurso;
        this.esObligatorio = "OBLIGATORIO".equals(tipoCurso);
    }

    @PrePersist
    public void prePersist() {
        if (this.esObligatorio == null) {
            this.esObligatorio = "OBLIGATORIO".equals(this.tipoCurso);
        }
        if (this.tipoCurso == null) {
            this.tipoCurso = this.esObligatorio ? "OBLIGATORIO" : "ELECTIVO";
        }
    }
}
