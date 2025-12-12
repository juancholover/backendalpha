package upeu.edu.pe.curriculum.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un sílabo que ha sido marcado como plantilla para ser publicado
 * en múltiples campus/filiales.
 * 
 * Workflow:
 * 1. Un Silabo en estado APROBADO puede convertirse en Plantilla
 * 2. La plantilla se publica en N campus (tabla SilaboPublicacion)
 * 3. Cada campus recibe una copia del sílabo base
 * 4. Campus puede adaptar fechas/evaluaciones, NO contenido académico
 */
@Entity
@Table(name = "silabo_plantilla", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"silabo_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class SilaboPlantilla extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Sílabo base que funciona como plantilla.
     * DEBE estar en estado APROBADO.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "silabo_id", nullable = false)
    private Silabo silabo;

    /**
     * Estado de la plantilla:
     * - ACTIVA: Disponible para publicación en campus
     * - OBSOLETA: Ya no se usa (nueva versión disponible)
     * - SUSPENDIDA: Temporalmente no disponible
     */
    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "ACTIVA"; // ACTIVA, OBSOLETA, SUSPENDIDA

    /**
     * Fecha desde la cual la plantilla está disponible para publicación.
     */
    @Column(name = "fecha_inicio_vigencia")
    private LocalDate fechaInicioVigencia;

    /**
     * Fecha hasta la cual la plantilla está disponible.
     */
    @Column(name = "fecha_fin_vigencia")
    private LocalDate fechaFinVigencia;

    /**
     * Usuario que autorizó la plantilla (rol sede_central).
     */
    @Column(name = "autorizado_por", length = 200)
    private String autorizadoPor;

    /**
     * Fecha de autorización.
     */
    @Column(name = "fecha_autorizacion")
    private LocalDate fechaAutorizacion;

    /**
     * Notas sobre la plantilla (instrucciones para campus).
     */
    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    /**
     * Define qué pueden modificar los campus:
     * - SOLO_FECHAS: Solo ajustar fechas de evaluaciones
     * - FECHAS_Y_PONDERACION: Fechas + porcentajes de evaluación
     * - SIN_MODIFICACION: Copia exacta, sin cambios permitidos
     */
    @Column(name = "nivel_flexibilidad", length = 30)
    private String nivelFlexibilidad = "FECHAS_Y_PONDERACION";

    /**
     * Publicaciones de esta plantilla en diferentes campus.
     */
    @OneToMany(mappedBy = "plantilla", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SilaboPublicacion> publicaciones = new ArrayList<>();

    /**
     * Marca la plantilla como obsoleta (reemplazada por nueva versión).
     */
    public void marcarObsoleta() {
        this.estado = "OBSOLETA";
        this.fechaFinVigencia = LocalDate.now();
    }

    /**
     * Verifica si la plantilla está vigente en una fecha dada.
     */
    public boolean estaVigenteEn(LocalDate fecha) {
        if (!"ACTIVA".equals(estado)) return false;
        
        boolean despuesInicio = fechaInicioVigencia == null || 
                                !fecha.isBefore(fechaInicioVigencia);
        boolean antesFin = fechaFinVigencia == null || 
                          !fecha.isAfter(fechaFinVigencia);
        
        return despuesInicio && antesFin;
    }

    /**
     * Verifica si el campus puede modificar el campo especificado.
     */
    public boolean puedeModificar(String campo) {
        return switch (nivelFlexibilidad) {
            case "SIN_MODIFICACION" -> false;
            case "SOLO_FECHAS" -> campo.toLowerCase().contains("fecha");
            case "FECHAS_Y_PONDERACION" -> 
                campo.toLowerCase().contains("fecha") || 
                campo.toLowerCase().contains("ponderacion");
            default -> false;
        };
    }
}
