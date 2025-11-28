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
        @UniqueConstraint(columnNames = { "codigo_seccion", "periodo_academico_id", "universidad_id" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class CursoOfertado extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidad_id", nullable = false)
    private Universidad universidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_curso_id", nullable = false)
    private PlanCurso planCurso; // La relación curso-plan que se está ofertando (incluye créditos, ciclo, tipo)

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

    @Column(name = "estado", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String estado; // ABIERTA, CERRADA, CANCELADA, EN_CURSO, FINALIZADA

    @Column(name = "observaciones", length = 500)
    @Normalize(Normalize.NormalizeType.SPACES_ONLY)
    private String observaciones;

    @OneToMany(mappedBy = "cursoOfertado", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Matricula> matriculas = new HashSet<>();

    @OneToMany(mappedBy = "cursoOfertado", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EvaluacionCriterio> evaluacionCriterios = new HashSet<>();

    public static CursoOfertado crear(Universidad universidad, PlanCurso planCurso,
            PeriodoAcademico periodoAcademico, String codigoSeccion,
            Integer capacidadMaxima, String modalidad, Profesor profesor,
            Localizacion localizacion) {
        CursoOfertado curso = new CursoOfertado();
        curso.setUniversidad(universidad);
        curso.setPlanCurso(planCurso);
        curso.setPeriodoAcademico(periodoAcademico);
        curso.setCodigoSeccion(codigoSeccion);
        curso.setCapacidadMaxima(capacidadMaxima);
        curso.setVacantesDisponibles(capacidadMaxima);
        curso.setModalidad(modalidad != null ? modalidad : "PRESENCIAL");
        curso.setProfesor(profesor);
        curso.setLocalizacion(localizacion);
        curso.setEstado("ABIERTA");
        return curso;
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
