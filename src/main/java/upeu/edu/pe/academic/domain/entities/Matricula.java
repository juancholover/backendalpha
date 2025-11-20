package upeu.edu.pe.academic.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;
import upeu.edu.pe.shared.annotations.Normalize;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "matricula", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"estudiante_id", "curso_ofertado_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"estudiante", "cursoOfertado", "evaluacionNotas"})
@EntityListeners(AuditListener.class)
public class Matricula extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_ofertado_id", nullable = false)
    private CursoOfertado cursoOfertado; // Curso ofertado en el que está matriculado

    // Campos legacy mantenidos para compatibilidad
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id")
    @Deprecated // Usar seccion.planAcademico.curso en su lugar
    private Curso curso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesor_id")
    @Deprecated // Usar seccion.profesor en su lugar
    private Profesor profesor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semestre_id")
    @Deprecated // Usar seccion.periodoAcademico en su lugar
    private Semestre semestre;

    @Column(name = "codigo_seccion", length = 10)
    @Deprecated // Usar seccion.codigoSeccion en su lugar
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String codigoSeccion; // A, B, C

    @Column(name = "fecha_matricula", nullable = false)
    private LocalDate fechaMatricula;

    @Column(name = "tipo_matricula", length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String tipoMatricula; // REGULAR, EXTRAORDINARIA

    @Column(name = "estado_matricula", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String estadoMatricula; // MATRICULADO, RETIRADO, ANULADO

    @Column(name = "fecha_retiro")
    private LocalDate fechaRetiro;

    @Column(name = "nota_final", precision = 5, scale = 2)
    private BigDecimal notaFinal; // Calculada automáticamente desde las evaluaciones

    @Column(name = "estado_aprobacion", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String estadoAprobacion; // APROBADO, DESAPROBADO, RETIRADO, PENDIENTE

    @Column(name = "inasistencias")
    private Integer inasistencias = 0;

    // Relación inversa con las notas de evaluación
    @OneToMany(mappedBy = "matricula", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EvaluacionNota> evaluacionNotas = new HashSet<>();

    /**
     * Constructor de conveniencia
     */
    public Matricula(Estudiante estudiante, CursoOfertado cursoOfertado) {
        this.estudiante = estudiante;
        this.cursoOfertado = cursoOfertado;
        this.fechaMatricula = LocalDate.now();
        this.estadoMatricula = "MATRICULADO";
        this.tipoMatricula = "REGULAR";
        this.inasistencias = 0;
        this.estadoAprobacion = "PENDIENTE";
    }

    @PrePersist
    public void prePersist() {
        if (this.fechaMatricula == null) {
            this.fechaMatricula = LocalDate.now();
        }
        if (this.estadoMatricula == null) {
            this.estadoMatricula = "MATRICULADO";
        }
        if (this.tipoMatricula == null) {
            this.tipoMatricula = "REGULAR";
        }
        if (this.inasistencias == null) {
            this.inasistencias = 0;
        }
        if (this.estadoAprobacion == null) {
            this.estadoAprobacion = "PENDIENTE";
        }
    }

    /**
     * Calcula la nota final basada en los criterios de evaluación
     */
    public void calcularNotaFinal() {
        if (evaluacionNotas == null || evaluacionNotas.isEmpty()) {
            return;
        }

        BigDecimal notaTotal = BigDecimal.ZERO;
        int pesoTotal = 0;

        for (EvaluacionNota evaluacionNota : evaluacionNotas) {
            if (evaluacionNota.getNotaFinal() != null && evaluacionNota.getCriterio() != null) {
                BigDecimal nota = evaluacionNota.getNotaFinal();
                int peso = evaluacionNota.getCriterio().getPeso();
                
                notaTotal = notaTotal.add(nota.multiply(new BigDecimal(peso)));
                pesoTotal += peso;
            }
        }

        if (pesoTotal > 0) {
            this.notaFinal = notaTotal.divide(new BigDecimal(pesoTotal), 2, BigDecimal.ROUND_HALF_UP);
            actualizarEstadoAprobacion();
        }
    }

    /**
     * Actualiza el estado de aprobación basado en la nota final
     */
    private void actualizarEstadoAprobacion() {
        if ("RETIRADO".equals(this.estadoMatricula)) {
            this.estadoAprobacion = "RETIRADO";
        } else if (this.notaFinal != null) {
            // Nota mínima aprobatoria es 11 (puede parametrizarse)
            if (this.notaFinal.compareTo(new BigDecimal("11")) >= 0) {
                this.estadoAprobacion = "APROBADO";
            } else {
                this.estadoAprobacion = "DESAPROBADO";
            }
        }
    }

    /**
     * Retira al estudiante del curso
     */
    public void retirar() {
        if ("RETIRADO".equals(this.estadoMatricula)) {
            throw new IllegalStateException("El estudiante ya está retirado");
        }
        this.estadoMatricula = "RETIRADO";
        this.fechaRetiro = LocalDate.now();
        this.estadoAprobacion = "RETIRADO";
    }
}
