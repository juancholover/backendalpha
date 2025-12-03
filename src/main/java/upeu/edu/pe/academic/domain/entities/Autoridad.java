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

public class Autoridad extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidad_id", nullable = false)
    private Universidad universidad; 

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

    @Column(name = "es_vigente")
    private Boolean esVigente; // TRUE si es la autoridad vigente para visualización rápida
    

    public Autoridad(Universidad universidad, Persona persona, TipoAutoridad tipoAutoridad, 
                     LocalDate fechaInicio, Boolean esVigente) {
        this.universidad = universidad;
        this.persona = persona;
        this.tipoAutoridad = tipoAutoridad;
        this.fechaInicio = fechaInicio;
        this.esVigente = esVigente;
    }
}
