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

/**
 * Entidad que representa una actividad de aprendizaje o evaluación dentro de una unidad.
 * Puede ser formativa (sin ponderación) o sumativa (con ponderación para nota final).
 */
@Entity
@Table(name = "silabo_actividad")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class SilaboActividad extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "silabo_unidad_id", nullable = false)
    private SilaboUnidad unidad;

    @Column(name = "tipo", nullable = false, length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String tipo; // FORMATIVA, SUMATIVA

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre; // Examen Parcial, Trabajo Final, Práctica 1

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "ponderacion", precision = 5, scale = 2)
    private BigDecimal ponderacion; // 0.00 - 100.00 (solo para SUMATIVA)

    @Column(name = "semana_programada")
    private Integer semanaProgramada;

    @Column(name = "instrumento_evaluacion", length = 100)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String instrumentoEvaluacion; // Rúbrica, Lista de cotejo, Examen escrito

    @Column(name = "indicadores", columnDefinition = "TEXT")
    private String indicadores; // Criterios de evaluación

    @Column(name = "criterios_evaluacion", columnDefinition = "TEXT")
    private String criteriosEvaluacion; // Criterios detallados de evaluación

    /**
     * Constructor para crear actividad con datos básicos
     */
    public SilaboActividad(SilaboUnidad unidad, String tipo, String nombre, Integer semanaProgramada) {
        this.unidad = unidad;
        this.tipo = tipo;
        this.nombre = nombre;
        this.semanaProgramada = semanaProgramada;
    }

    // Métodos de negocio

    /**
     * Verifica si la actividad es formativa (no calificada)
     */
    public boolean esFormativa() {
        return "FORMATIVA".equals(this.tipo);
    }

    /**
     * Verifica si la actividad es sumativa (calificada)
     */
    public boolean esSumativa() {
        return "SUMATIVA".equals(this.tipo);
    }

    /**
     * Valida que la ponderación sea correcta
     */
    public void validarPonderacion() {
        if (esSumativa()) {
            if (ponderacion == null) {
                throw new IllegalArgumentException(
                    "Actividad SUMATIVA debe tener ponderación"
                );
            }
            if (ponderacion.compareTo(BigDecimal.ZERO) < 0 || 
                ponderacion.compareTo(new BigDecimal("100")) > 0) {
                throw new IllegalArgumentException(
                    "Ponderación debe estar entre 0 y 100"
                );
            }
        } else if (esFormativa()) {
            // Las actividades formativas no deben tener ponderación
            if (ponderacion != null && ponderacion.compareTo(BigDecimal.ZERO) > 0) {
                throw new IllegalArgumentException(
                    "Actividad FORMATIVA no debe tener ponderación"
                );
            }
        }
    }

    /**
     * Valida que la semana programada esté dentro de la unidad
     */
    public void validarSemanaProgramada() {
        if (semanaProgramada != null && unidad != null) {
            if (!unidad.incluyeSemana(semanaProgramada)) {
                throw new IllegalArgumentException(
                    "La semana programada debe estar dentro del rango de la unidad (" +
                    unidad.getSemanaInicio() + "-" + unidad.getSemanaFin() + ")"
                );
            }
        }
    }

    @PrePersist
    @PreUpdate
    public void validar() {
        validarPonderacion();
        validarSemanaProgramada();
    }
}
