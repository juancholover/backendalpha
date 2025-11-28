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
@Table(name = "evaluacion_criterio", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "curso_ofertado_id", "nombre", "universidad_id" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class EvaluacionCriterio extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidad_id", nullable = false)
    private Universidad universidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_ofertado_id", nullable = false)
    private CursoOfertado cursoOfertado; // Reglas de calificación del curso ofertado

    @Column(name = "nombre", nullable = false, length = 100)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String nombre; // Ej: "Examen Parcial", "Trabajo Final", "Prácticas"

    @Column(name = "descripcion", length = 500)
    @Normalize(Normalize.NormalizeType.SPACES_ONLY)
    private String descripcion;

    @Column(name = "peso", nullable = false)
    private Integer peso; // Porcentaje (0-100) que representa en la nota final

    @Column(name = "tipo_evaluacion", length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String tipoEvaluacion; // EXAMEN, PRACTICA, TAREA, PROYECTO, PARTICIPACION

    @Column(name = "nota_maxima", nullable = false)
    private Integer notaMaxima = 20; // Escala máxima (ej: 20, 100)

    @Column(name = "nota_minima_aprobatoria")
    private Integer notaMinimaAprobatoria = 11; // Nota mínima para aprobar este criterio

    @Column(name = "orden")
    private Integer orden; // Para ordenar los criterios en la UI

    @Column(name = "es_recuperable")
    private Boolean esRecuperable = false; // Si se puede recuperar esta evaluación

    @Column(name = "estado", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String estado; // ACTIVO, ELIMINADO

    @OneToMany(mappedBy = "criterio", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EvaluacionNota> evaluacionNotas = new HashSet<>();

    public static EvaluacionCriterio crear(Universidad universidad, CursoOfertado cursoOfertado, String nombre,
            Integer peso, String tipoEvaluacion) {
        EvaluacionCriterio criterio = new EvaluacionCriterio();
        criterio.setUniversidad(universidad);
        criterio.setCursoOfertado(cursoOfertado);
        criterio.setNombre(nombre);
        criterio.setPeso(peso);
        criterio.setTipoEvaluacion(tipoEvaluacion);

        // Defaults
        criterio.setNotaMaxima(20);
        criterio.setNotaMinimaAprobatoria(11);
        criterio.setEsRecuperable(false);
        criterio.setEstado("ACTIVO");

        criterio.validarPeso();

        return criterio;
    }

    /**
     * Validación: El peso debe estar entre 0 y 100
     */
    public void validarPeso() {
        if (peso != null && (peso < 0 || peso > 100)) {
            throw new IllegalArgumentException("El peso debe estar entre 0 y 100");
        }
    }

    /**
     * Validación: El peso debe estar entre 0 y 100
     */
}
