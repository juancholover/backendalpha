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
        @UniqueConstraint(columnNames = { "curso_id", "curso_requisito_id", "tipo_requisito", "universidad_id" })
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

    public RequisitoCurso(Universidad universidad, Curso curso, Curso cursoRequisito, String tipoRequisito) {
        this.universidad = universidad;
        this.curso = curso;
        this.cursoRequisito = cursoRequisito;
        this.tipoRequisito = tipoRequisito;
        this.esObligatorio = true;
    }

    public RequisitoCurso(Universidad universidad, Curso curso, Curso cursoRequisito, String tipoRequisito,
            Boolean esObligatorio, Integer notaMinimaRequerida) {
        this.universidad = universidad;
        this.curso = curso;
        this.cursoRequisito = cursoRequisito;
        this.tipoRequisito = tipoRequisito;
        this.esObligatorio = esObligatorio;
        this.notaMinimaRequerida = notaMinimaRequerida;
    }

    public static RequisitoCurso crear(Universidad universidad, Curso curso, Curso cursoRequisito,
            String tipoRequisito, Boolean esObligatorio,
            Integer notaMinimaRequerida, String observacion) {
        RequisitoCurso requisito = new RequisitoCurso();
        requisito.setUniversidad(universidad);
        requisito.setCurso(curso);
        requisito.setCursoRequisito(cursoRequisito);

        // Default values logic
        requisito.setTipoRequisito(tipoRequisito != null ? tipoRequisito : "PRERREQUISITO");
        requisito.setEsObligatorio(esObligatorio != null ? esObligatorio : true);

        requisito.setNotaMinimaRequerida(notaMinimaRequerida);
        requisito.setObservacion(observacion);

        return requisito;
    }
}
