package upeu.edu.pe.academic.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;

import java.time.LocalDate;

@Entity
@Table(name = "autoridad")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
/**
 * NOTA: Historial de Autoridades - SIN restricción única en JPA
 * 
 * Esta entidad permite el historial completo de cargos sin restricciones JPA porque:
 * - La misma persona puede ocupar el mismo cargo varias veces en diferentes periodos
 *   (ejemplo: Rector 2020-2024, luego Rector 2025-2029)
 * - La misma universidad puede tener múltiples rectores históricos 
 *   (diferentes personas o la misma persona en distintos periodos)
 * 
 * REGLAS DE NEGOCIO (validadas en AutoridadService):
 * 1. Solo puede haber UNA autoridad ACTIVA (activo=TRUE) por tipo_autoridad_id + universidad_id simultáneamente
 * 2. Antes de crear/activar una autoridad, el servicio valida que no exista otra autoridad activa del mismo tipo
 * 3. Al designar una nueva autoridad del mismo tipo, la anterior se desactiva automáticamente 
 *    (activo=FALSE, fechaFin=hoy)
 * 
 * INTEGRIDAD EN BASE DE DATOS (PostgreSQL - índice único parcial):
 * Para forzar la regla #1 a nivel de BD, crear este índice en la migración SQL:
 * 
 *   CREATE UNIQUE INDEX idx_autoridad_activa_unica 
 *   ON autoridad(tipo_autoridad_id, universidad_id) 
 *   WHERE activo = TRUE;
 * 
 * Este índice único parcial permite múltiples registros históricos (activo=FALSE) pero garantiza 
 * que solo uno esté activo por cargo/universidad. PostgreSQL 9.0+.
 */
public class Autoridad extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidad_id", nullable = false)
    private Universidad universidad; // Aislamiento multi-tenant

    // Conectamos con la persona (que ya tiene nombre, foto, DNI)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona; 

    // Conectamos con el cargo dinámico (Rector, Presidente, etc.)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_autoridad_id", nullable = false)
    private TipoAutoridad tipoAutoridad;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin; // Si es NULL, sigue en el cargo actualmente

    @Column(name = "activo")
    private Boolean activo; // TRUE si es la autoridad vigente para visualización rápida
    
    @Column(name = "resolucion_designacion", length = 100)
    private String resolucionDesignacion; // Opcional: Documento legal

    /**
     * Constructor de conveniencia para crear una nueva autoridad
     */
    public Autoridad(Universidad universidad, Persona persona, TipoAutoridad tipoAutoridad, 
                     LocalDate fechaInicio, Boolean activo) {
        this.universidad = universidad;
        this.persona = persona;
        this.tipoAutoridad = tipoAutoridad;
        this.fechaInicio = fechaInicio;
        this.activo = activo;
    }
}
