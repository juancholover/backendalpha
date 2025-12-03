package upeu.edu.pe.academic.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una unidad de aprendizaje dentro de un sílabo.
 * Cada unidad agrupa contenidos, logros y actividades de un periodo del curso.
 */
@Entity
@Table(name = "silabo_unidad")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class SilaboUnidad extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "silabo_id", nullable = false)
    private Silabo silabo;

    @Column(name = "numero_unidad", nullable = false)
    private Integer numeroUnidad;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "semana_inicio", nullable = false)
    private Integer semanaInicio;

    @Column(name = "semana_fin", nullable = false)
    private Integer semanaFin;

    @Column(name = "contenidos", columnDefinition = "TEXT")
    private String contenidos; // Temas a desarrollar

    @Column(name = "logro_aprendizaje", columnDefinition = "TEXT")
    private String logroAprendizaje; // Resultado de aprendizaje esperado

    @Column(name = "estrategias_ensenanza", columnDefinition = "TEXT")
    private String estrategiasEnsenanza; // Metodología específica de la unidad

    @OneToMany(mappedBy = "unidad", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SilaboActividad> actividades = new ArrayList<>();

    /**
     * Constructor para crear unidad con datos básicos
     */
    public SilaboUnidad(Silabo silabo, Integer numeroUnidad, String titulo, 
                        Integer semanaInicio, Integer semanaFin) {
        this.silabo = silabo;
        this.numeroUnidad = numeroUnidad;
        this.titulo = titulo;
        this.semanaInicio = semanaInicio;
        this.semanaFin = semanaFin;
    }

    // Métodos de negocio

    /**
     * Calcula la duración de la unidad en semanas
     */
    public int getDuracionSemanas() {
        return semanaFin - semanaInicio + 1;
    }

    /**
     * Agrega una actividad a la unidad
     */
    public void agregarActividad(SilaboActividad actividad) {
        actividades.add(actividad);
        actividad.setUnidad(this);
    }

    /**
     * Valida que el rango de semanas sea válido
     */
    public void validarRangoSemanas() {
        if (semanaInicio == null || semanaInicio < 1) {
            throw new IllegalArgumentException("Semana de inicio debe ser mayor a 0");
        }
        if (semanaFin == null || semanaFin > 20) {
            throw new IllegalArgumentException("Semana de fin no puede ser mayor a 20");
        }
        if (semanaInicio > semanaFin) {
            throw new IllegalArgumentException("Semana de inicio no puede ser mayor a semana de fin");
        }
    }

    /**
     * Verifica si una semana específica está dentro del rango de la unidad
     */
    public boolean incluyeSemana(int semana) {
        return semana >= semanaInicio && semana <= semanaFin;
    }

    @PrePersist
    @PreUpdate
    public void validar() {
        validarRangoSemanas();
    }
}
