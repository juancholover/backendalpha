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
import java.math.RoundingMode;
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
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class Matricula extends AuditableEntity {

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
    @JoinColumn(name = "curso_ofertado_id", nullable = false)
    private CursoOfertado cursoOfertado;

    @Column(name = "fecha_matricula", nullable = false)
    private LocalDate fechaMatricula = LocalDate.now();

    @Column(name = "tipo_matricula", length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String tipoMatricula = "REGULAR"; // REGULAR, EXTRAORDINARIA

    // ==================== CONTROL DE CRÉDITOS ====================

    @Column(name = "creditos_matriculados")
    private Integer creditosMatriculados; // Créditos del curso matriculado

    @Column(name = "estado_matricula", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String estadoMatricula = "MATRICULADO"; // MATRICULADO, RETIRADO, ANULADO

    @Column(name = "fecha_retiro")
    private LocalDate fechaRetiro;

    @Column(name = "nota_final", precision = 5, scale = 2)
    private BigDecimal notaFinal; 

    @Column(name = "estado_aprobacion", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String estadoAprobacion = "PENDIENTE"; // APROBADO, DESAPROBADO, RETIRADO, PENDIENTE

    @Column(name = "inasistencias")
    private Integer inasistencias = 0;

    // Relación inversa con las notas de evaluación
    @OneToMany(mappedBy = "matricula", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EvaluacionNota> evaluacionNotas = new HashSet<>();

    /**
     * Constructor de conveniencia
     */
    public Matricula(Universidad universidad, Estudiante estudiante, CursoOfertado cursoOfertado) {
        this.universidad = universidad;
        this.estudiante = estudiante;
        this.cursoOfertado = cursoOfertado;
        this.fechaMatricula = LocalDate.now();
        this.estadoMatricula = "MATRICULADO";
        this.tipoMatricula = "REGULAR";
        this.inasistencias = 0;
        this.estadoAprobacion = "PENDIENTE";
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
            // Nota Final = Suma(Nota*Peso) / Suma(Pesos)
            // Se usa RoundingMode.HALF_UP para redondeo estándar (10.5 -> 11, 10.4 -> 10)
            this.notaFinal = notaTotal.divide(new BigDecimal(pesoTotal), 2, RoundingMode.HALF_UP);
            actualizarEstadoAprobacion();
        }
    }

    private void actualizarEstadoAprobacion() {
        if ("RETIRADO".equals(this.estadoMatricula)) {
            this.estadoAprobacion = "RETIRADO";
        } else if (this.notaFinal != null) {
            // Nota mínima aprobatoria parametrizable (ej. 11 o 13)
            // Aquí asumimos 11 como base, pero debería venir de una configuración
            if (this.notaFinal.compareTo(new BigDecimal("11")) >= 0) {
                this.estadoAprobacion = "APROBADO";
            } else {
                this.estadoAprobacion = "DESAPROBADO";
            }
        }
    }
    
    public void retirar() {
        if ("RETIRADO".equals(this.estadoMatricula)) {
            throw new IllegalStateException("El estudiante ya está retirado");
        }
        this.estadoMatricula = "RETIRADO";
        this.fechaRetiro = LocalDate.now();
        this.estadoAprobacion = "RETIRADO";
    }
}
