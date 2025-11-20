package upeu.edu.pe.academic.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;
import upeu.edu.pe.shared.annotations.Normalize;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "curso_ofertado", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"codigo_seccion", "periodo_academico_id", "universidad_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"universidad", "planAcademico", "periodoAcademico", "profesor", "localizacion", "matriculas", "evaluacionCriterios"})
@EntityListeners(AuditListener.class)
public class CursoOfertado extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidad_id", nullable = false)
    private Universidad universidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_academico_id", nullable = false)
    private PlanAcademico planAcademico; // El curso del plan que se está ofertando

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periodo_academico_id", nullable = false)
    private PeriodoAcademico periodoAcademico; // En qué período se dicta

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesor_id")
    private Profesor profesor; // Profesor asignado

    @Column(name = "codigo_seccion", nullable = false, length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String codigoSeccion; // Ej: A, B, C, 01, 02

    @Column(name = "capacidad_maxima", nullable = false)
    private Integer capacidadMaxima;

    @Column(name = "vacantes_disponibles", nullable = false)
    private Integer vacantesDisponibles;

    @Column(name = "modalidad", length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String modalidad; // PRESENCIAL, VIRTUAL, HIBRIDA

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "localizacion_id")
    private Localizacion localizacion; // Aula asignada

    @Column(name = "horario", length = 500)
    private String horario; // JSON o texto con horarios (ej: "LUN 08:00-10:00, MIE 08:00-10:00")

    @Column(name = "estado", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String estado; // ABIERTA, CERRADA, CANCELADA, EN_CURSO, FINALIZADA

    @Column(name = "observaciones", length = 500)
    @Normalize(Normalize.NormalizeType.SPACES_ONLY)
    private String observaciones;

    // Relaciones inversas
    @OneToMany(mappedBy = "cursoOfertado", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Matricula> matriculas = new HashSet<>();

    @OneToMany(mappedBy = "cursoOfertado", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EvaluacionCriterio> evaluacionCriterios = new HashSet<>();

    /**
     * Constructor de conveniencia
     */
    public CursoOfertado(Universidad universidad, PlanAcademico planAcademico, 
                  PeriodoAcademico periodoAcademico, String codigoSeccion, 
                  Integer capacidadMaxima) {
        this.universidad = universidad;
        this.planAcademico = planAcademico;
        this.periodoAcademico = periodoAcademico;
        this.codigoSeccion = codigoSeccion;
        this.capacidadMaxima = capacidadMaxima;
        this.vacantesDisponibles = capacidadMaxima;
        this.estado = "ABIERTA";
        this.modalidad = "PRESENCIAL";
    }

    @PrePersist
    public void prePersist() {
        if (this.estado == null) {
            this.estado = "ABIERTA";
        }
        if (this.vacantesDisponibles == null && this.capacidadMaxima != null) {
            this.vacantesDisponibles = this.capacidadMaxima;
        }
        if (this.modalidad == null) {
            this.modalidad = "PRESENCIAL";
        }
    }

    /**
     * Método para reducir vacantes al matricular un estudiante
     */
    public void reducirVacantes() {
        if (this.vacantesDisponibles > 0) {
            this.vacantesDisponibles--;
        } else {
            throw new IllegalStateException("No hay vacantes disponibles en esta sección");
        }
    }

    /**
     * Método para aumentar vacantes al desmatricular un estudiante
     */
    public void aumentarVacantes() {
        if (this.vacantesDisponibles < this.capacidadMaxima) {
            this.vacantesDisponibles++;
        }
    }

    /**
     * Verifica si hay cupo disponible
     */
    public boolean hayCupoDisponible() {
        return this.vacantesDisponibles > 0 && "ABIERTA".equals(this.estado);
    }
}
