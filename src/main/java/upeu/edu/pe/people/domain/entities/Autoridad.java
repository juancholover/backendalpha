package upeu.edu.pe.people.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;
import upeu.edu.pe.core.domain.entities.Persona;
import upeu.edu.pe.core.domain.entities.UnidadOrganizativa;
import upeu.edu.pe.curriculum.domain.entities.ProgramaAcademico;

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

    // Conectamos con la persona (que ya tiene nombre, foto, DNI)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona; 

    // Conectamos con el cargo dinámico (Rector, Presidente, etc.)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_autoridad_id", nullable = false)
    private TipoAutoridad tipoAutoridad;

    // Unidad organizativa (Facultad, Escuela) - NULL para autoridades universitarias (Rector)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_organizativa_id")
    private UnidadOrganizativa unidadOrganizativa;

    // Programa académico - Para directores de programa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programa_academico_id")
    private ProgramaAcademico programaAcademico;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin; // Si es NULL, sigue en el cargo actualmente

    @Column(name = "es_vigente")
    private Boolean esVigente; // TRUE si es la autoridad vigente para visualización rápida
    
    @Column(name = "resolucion_designacion", length = 100)
    private String resolucionDesignacion; // Número de resolución que designa a la autoridad
    
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones; // Observaciones adicionales

    public Autoridad(Persona persona, TipoAutoridad tipoAutoridad, 
                     LocalDate fechaInicio, Boolean esVigente) {
        this.persona = persona;
        this.tipoAutoridad = tipoAutoridad;
        this.fechaInicio = fechaInicio;
        this.esVigente = esVigente;
    }

    public Autoridad(Persona persona, TipoAutoridad tipoAutoridad, 
                     UnidadOrganizativa unidadOrganizativa, ProgramaAcademico programaAcademico,
                     LocalDate fechaInicio, Boolean esVigente) {
        this.persona = persona;
        this.tipoAutoridad = tipoAutoridad;
        this.unidadOrganizativa = unidadOrganizativa;
        this.programaAcademico = programaAcademico;
        this.fechaInicio = fechaInicio;
        this.esVigente = esVigente;
    }
}
