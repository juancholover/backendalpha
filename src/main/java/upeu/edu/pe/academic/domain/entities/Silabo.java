package upeu.edu.pe.academic.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa el sílabo oficial de un curso.
 * Define competencias, unidades, actividades y criterios de evaluación.
 * Se mantiene un sílabo por curso por año académico para estandarización institucional.
 */
@Entity
@Table(name = "silabo", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"curso_id", "anio_academico", "version"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class Silabo extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidad_id", nullable = false)
    private Universidad universidad;

    @Column(name = "anio_academico", nullable = false, length = 10)
    private String anioAcademico; // 2025, 2026

    @Column(name = "version", nullable = false)
    private Integer version = 1;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado; // BORRADOR, EN_REVISION, APROBADO, VIGENTE, OBSOLETO

    @Column(name = "porcentaje_calidad", precision = 5, scale = 2)
    private BigDecimal porcentajeCalidad; // 0.00 - 100.00

    @Column(name = "fecha_aprobacion")
    private LocalDate fechaAprobacion;

    @Column(name = "aprobado_por", length = 200)
    private String aprobadoPor; // Nombre del aprobador

    @Column(name = "observaciones", length = 1000)
    private String observaciones;

    @Column(name = "competencias", columnDefinition = "TEXT")
    private String competencias; // Descripción de competencias a desarrollar

    @Column(name = "sumilla", columnDefinition = "TEXT")
    private String sumilla; // Resumen del curso

    @Column(name = "bibliografia", columnDefinition = "TEXT")
    private String bibliografia; // Referencias bibliográficas

    @Column(name = "metodologia", columnDefinition = "TEXT")
    private String metodologia; // Estrategias metodológicas

    @Column(name = "recursos_didacticos", columnDefinition = "TEXT")
    private String recursosDidacticos; // Materiales y recursos

    @OneToMany(mappedBy = "silabo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SilaboUnidad> unidades = new ArrayList<>();

    @OneToMany(mappedBy = "silabo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SilaboHistorial> historial = new ArrayList<>();

    /**
     * Constructor para crear sílabo nuevo
     */
    public Silabo(Universidad universidad, Curso curso, String anioAcademico) {
        this.universidad = universidad;
        this.curso = curso;
        this.anioAcademico = anioAcademico;
        this.version = 1;
        this.estado = "BORRADOR";
    }

    // Métodos de negocio

    /**
     * Verifica si el sílabo está aprobado
     */
    public boolean estaAprobado() {
        return "APROBADO".equals(this.estado) || "VIGENTE".equals(this.estado);
    }

    /**
     * Verifica si el sílabo está vigente
     */
    public boolean estaVigente() {
        return "VIGENTE".equals(this.estado);
    }

    /**
     * Verifica si el sílabo puede ser modificado
     */
    public boolean esModificable() {
        return "BORRADOR".equals(this.estado) || "EN_REVISION".equals(this.estado);
    }

    /**
     * Verifica si el sílabo está completo para ser enviado a revisión
     */
    public boolean estaCompleto() {
        return this.competencias != null && !this.competencias.isBlank()
                && this.sumilla != null && !this.sumilla.isBlank()
                && this.unidades != null && !this.unidades.isEmpty();
    }

    /**
     * Envía el sílabo a revisión
     */
    public void enviarARevision() {
        if (!"BORRADOR".equals(this.estado)) {
            throw new IllegalStateException("Solo se pueden enviar a revisión sílabos en borrador");
        }
        this.estado = "EN_REVISION";
    }

    /**
     * Aprueba el sílabo
     */
    public void aprobar() {
        if (!"EN_REVISION".equals(this.estado)) {
            throw new IllegalStateException("Solo se pueden aprobar sílabos en revisión");
        }
        this.estado = "APROBADO";
        this.fechaAprobacion = LocalDate.now();
    }

    /**
     * Activa el sílabo como vigente
     */
    public void activar() {
        if (!estaAprobado()) {
            throw new IllegalStateException("Solo se pueden activar sílabos aprobados");
        }
        this.estado = "VIGENTE";
    }

    /**
     * Marca el sílabo como obsoleto
     */
    public void marcarObsoleto() {
        if (!estaVigente()) {
            throw new IllegalStateException("Solo se pueden marcar como obsoletos sílabos vigentes");
        }
        this.estado = "OBSOLETO";
    }

    /**
     * Rechaza el sílabo y lo devuelve a borrador
     */
    public void rechazar() {
        if (!"EN_REVISION".equals(this.estado)) {
            throw new IllegalStateException("Solo se pueden rechazar sílabos en revisión");
        }
        this.estado = "BORRADOR";
    }

    /**
     * Agrega una unidad al sílabo
     */
    public void agregarUnidad(SilaboUnidad unidad) {
        if (!esModificable()) {
            throw new IllegalStateException("No se pueden agregar unidades a un sílabo no modificable");
        }
        unidades.add(unidad);
        unidad.setSilabo(this);
    }

    /**
     * Calcula el total de semanas del sílabo
     */
    public int getTotalSemanas() {
        return unidades.stream()
                .mapToInt(u -> u.getSemanaFin() - u.getSemanaInicio() + 1)
                .sum();
    }

    /**
     * Calcula el porcentaje total de evaluación
     */
    public BigDecimal getPorcentajeEvaluacionTotal() {
        return unidades.stream()
                .flatMap(u -> u.getActividades().stream())
                .filter(a -> a.esSumativa())
                .map(SilaboActividad::getPonderacion)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Valida que el sílabo esté completo antes de enviar a revisión
     */
    public void validarCompletitud() {
        if (sumilla == null || sumilla.isBlank()) {
            throw new IllegalStateException("El sílabo debe tener sumilla");
        }
        if (competencias == null || competencias.isBlank()) {
            throw new IllegalStateException("El sílabo debe tener competencias definidas");
        }
        if (unidades.isEmpty()) {
            throw new IllegalStateException("El sílabo debe tener al menos una unidad");
        }

        BigDecimal totalEvaluacion = getPorcentajeEvaluacionTotal();
        if (totalEvaluacion.compareTo(new BigDecimal("100")) != 0) {
            throw new IllegalStateException(
                "Las evaluaciones sumativas deben sumar 100% (actual: " + totalEvaluacion + "%)"
            );
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.estado == null) {
            this.estado = "BORRADOR";
        }
        if (this.version == null) {
            this.version = 1;
        }
    }
}
