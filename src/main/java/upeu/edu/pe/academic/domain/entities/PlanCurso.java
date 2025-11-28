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
        @UniqueConstraint(columnNames = { "plan_academico_id", "curso_id", "universidad_id" })
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

    public static PlanCurso crear(Universidad universidad, PlanAcademico planAcademico, Curso curso,
            Integer creditos, Integer ciclo, String tipoCurso, Boolean esObligatorio) {
        PlanCurso planCurso = new PlanCurso();
        planCurso.setUniversidad(universidad);
        planCurso.setPlanAcademico(planAcademico);
        planCurso.setCurso(curso);
        planCurso.setCreditos(creditos);
        planCurso.setCiclo(ciclo);

        // Default values logic
        if (esObligatorio != null) {
            planCurso.setEsObligatorio(esObligatorio);
            planCurso.setTipoCurso(tipoCurso != null ? tipoCurso : (esObligatorio ? "OBLIGATORIO" : "ELECTIVO"));
        } else {
            // If esObligatorio is null, derive from tipoCurso
            String tipo = tipoCurso != null ? tipoCurso : "OBLIGATORIO";
            planCurso.setTipoCurso(tipo);
            planCurso.setEsObligatorio("OBLIGATORIO".equals(tipo));
        }

        return planCurso;
    }
}
