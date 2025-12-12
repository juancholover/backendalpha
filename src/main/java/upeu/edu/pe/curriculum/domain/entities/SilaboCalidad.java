package upeu.edu.pe.curriculum.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;

import java.time.LocalDateTime;

/**
 * Entidad que almacena la evaluación de calidad de un sílabo.
 * Un sílabo debe tener al menos 80% de calidad para poder ser publicado.
 */
@Entity
@Table(name = "silabo_calidad")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class SilaboCalidad extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "silabo_id", nullable = false)
    private Silabo silabo;

    @Column(name = "puntaje_total", nullable = false)
    private Integer puntajeTotal; // 0-100

    @Column(name = "evaluado_por", length = 100)
    private String evaluadoPor; // "IA" o nombre de la persona

    @Column(name = "fecha_evaluacion", nullable = false)
    private LocalDateTime fechaEvaluacion;

    // Criterios de calidad (cada uno de 0-100)
    @Column(name = "descripcion_score")
    private Integer descripcionScore; // Claridad y completitud de la descripción

    @Column(name = "objetivos_score")
    private Integer objetivosScore; // Objetivos medibles y alcanzables

    @Column(name = "bibliografia_score")
    private Integer bibliografiaScore; // Bibliografía actualizada y relevante

    @Column(name = "evaluaciones_score")
    private Integer evaluacionesScore; // Sistema de evaluación completo

    @Column(name = "metodologia_score")
    private Integer metodologiaScore; // Metodología bien definida

    @Column(name = "competencias_score")
    private Integer competenciasScore; // Competencias claras

    @Column(name = "unidades_score")
    private Integer unidadesScore; // Unidades bien estructuradas

    @Column(name = "actividades_score")
    private Integer actividadesScore; // Actividades coherentes

    @Column(name = "aprobado", nullable = false)
    private Boolean aprobado; // true si puntajeTotal >= 80

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones; // Comentarios y recomendaciones

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "detalles_evaluacion", columnDefinition = "JSONB")
    private String detallesEvaluacion; // JSON con detalles adicionales

    /**
     * Calcula si el sílabo está aprobado (>= 80%)
     */
    @PrePersist
    @PreUpdate
    public void calcularAprobacion() {
        this.aprobado = this.puntajeTotal != null && this.puntajeTotal >= 80;
    }

    /**
     * Calcula el puntaje total promedio de todos los criterios
     */
    public void calcularPuntajeTotal() {
        int totalCriterios = 0;
        int sumaPuntajes = 0;

        if (descripcionScore != null) {
            sumaPuntajes += descripcionScore;
            totalCriterios++;
        }
        if (objetivosScore != null) {
            sumaPuntajes += objetivosScore;
            totalCriterios++;
        }
        if (bibliografiaScore != null) {
            sumaPuntajes += bibliografiaScore;
            totalCriterios++;
        }
        if (evaluacionesScore != null) {
            sumaPuntajes += evaluacionesScore;
            totalCriterios++;
        }
        if (metodologiaScore != null) {
            sumaPuntajes += metodologiaScore;
            totalCriterios++;
        }
        if (competenciasScore != null) {
            sumaPuntajes += competenciasScore;
            totalCriterios++;
        }
        if (unidadesScore != null) {
            sumaPuntajes += unidadesScore;
            totalCriterios++;
        }
        if (actividadesScore != null) {
            sumaPuntajes += actividadesScore;
            totalCriterios++;
        }

        this.puntajeTotal = totalCriterios > 0 ? sumaPuntajes / totalCriterios : 0;
    }
}
