package upeu.edu.pe.security.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;
import upeu.edu.pe.shared.annotations.Normalize;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "rol", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"nombre", "universidad_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"universidad", "rolPermisos"})
@EntityListeners(AuditListener.class)
public class Rol extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidad_id", nullable = false)
    private Universidad universidad;

    @Column(name = "nombre", nullable = false, length = 100)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String nombre; // ADMIN, PROFESOR, ESTUDIANTE, CAJERO, etc.

    @Column(name = "descripcion", length = 255)
    @Normalize(Normalize.NormalizeType.SPACES_ONLY)
    private String descripcion;

    @Column(name = "es_sistema", nullable = false)
    private Boolean esSistema = false; // true para roles predefinidos que no se pueden eliminar

    // Relación con RolPermiso
    @OneToMany(mappedBy = "rol", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RolPermiso> rolPermisos = new HashSet<>();

    /**
     * Constructor de conveniencia para crear un rol con universidad y nombre
     */
    public Rol(Universidad universidad, String nombre, String descripcion) {
        this.universidad = universidad;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.esSistema = false;
    }

    /**
     * Constructor para roles del sistema
     */
    public Rol(Universidad universidad, String nombre, String descripcion, Boolean esSistema) {
        this.universidad = universidad;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.esSistema = esSistema;
    }
}
