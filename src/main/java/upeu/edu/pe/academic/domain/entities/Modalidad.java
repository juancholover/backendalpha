package upeu.edu.pe.academic.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;
import upeu.edu.pe.shared.annotations.Normalize;

/**
 * Entidad que representa la modalidad de dictado de cursos.
 * Define cómo se imparte un curso: PRESENCIAL, VIRTUAL, SEMIPRESENCIAL, HÍBRIDA.
 * Se asocia a CursoOfertado para determinar recursos y logística necesaria.
 */
@Entity
@Table(name = "modalidad", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"codigo"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class Modalidad extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidad_id", nullable = false)
    private Universidad universidad;

    @Column(name = "codigo", nullable = false, length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String codigo; // PRES, VIRT, SEMI, HIBR

    @Column(name = "nombre", nullable = false, length = 100)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String nombre; // Presencial, Virtual, Semipresencial, Híbrida

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "requiere_aula", nullable = false)
    private Boolean requiereAula = true;

    @Column(name = "requiere_plataforma", nullable = false)
    private Boolean requierePlataforma = false;

    @Column(name = "porcentaje_presencialidad")
    private Integer porcentajePresencialidad; // 0-100 (útil para híbridas)

    @Column(name = "color_hex", length = 7)
    private String colorHex; // Para UI: #FF5733

    /**
     * Constructor para crear modalidad con datos básicos
     */
    public Modalidad(Universidad universidad, String codigo, String nombre, 
                     String descripcion, Boolean requiereAula, 
                     Boolean requierePlataforma, Integer porcentajePresencialidad) {
        this.universidad = universidad;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.requiereAula = requiereAula;
        this.requierePlataforma = requierePlataforma;
        this.porcentajePresencialidad = porcentajePresencialidad;
    }

    // Métodos de negocio

    /**
     * Verifica si la modalidad es 100% presencial
     */
    public boolean esPresencial() {
        return "PRES".equals(this.codigo);
    }

    /**
     * Verifica si la modalidad es 100% virtual
     */
    public boolean esVirtual() {
        return "VIRT".equals(this.codigo);
    }

    /**
     * Verifica si la modalidad es semipresencial
     */
    public boolean esSemipresencial() {
        return "SEMI".equals(this.codigo);
    }

    /**
     * Verifica si la modalidad es híbrida
     */
    public boolean esHibrida() {
        return "HIBR".equals(this.codigo);
    }

    /**
     * Indica si necesita infraestructura física (aulas)
     */
    public boolean necesitaInfraestructuraFisica() {
        return requiereAula != null && requiereAula;
    }

    /**
     * Indica si necesita plataforma digital (LMS, Zoom, etc.)
     */
    public boolean necesitaPlataformaDigital() {
        return requierePlataforma != null && requierePlataforma;
    }

    /**
     * Valida que el porcentaje de presencialidad sea coherente
     */
    @PrePersist
    @PreUpdate
    public void validar() {
        if (porcentajePresencialidad != null) {
            if (porcentajePresencialidad < 0 || porcentajePresencialidad > 100) {
                throw new IllegalArgumentException(
                    "El porcentaje de presencialidad debe estar entre 0 y 100"
                );
            }
        }

        // Validar coherencia entre código y flags
        if (esPresencial() && !requiereAula) {
            throw new IllegalStateException(
                "Modalidad PRESENCIAL debe requerir aula física"
            );
        }

        if (esVirtual() && !requierePlataforma) {
            throw new IllegalStateException(
                "Modalidad VIRTUAL debe requerir plataforma digital"
            );
        }
    }
}
