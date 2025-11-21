package upeu.edu.pe.security.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;
import upeu.edu.pe.shared.annotations.Normalize;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permiso")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class Permiso extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "nombre_clave", unique = true, nullable = false, length = 100)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String nombreClave; // Ej: EDITAR_NOTAS, VER_REPORTES, GESTIONAR_USUARIOS

    @Column(name = "descripcion", length = 255)
    @Normalize(Normalize.NormalizeType.SPACES_ONLY)
    private String descripcion;

    @Column(name = "modulo", length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String modulo; // ACADEMICO, FINANZAS, SEGURIDAD, REPORTES

    @Column(name = "recurso", length = 100)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String recurso; // NOTAS, USUARIOS, PAGOS, CURSOS

    @Column(name = "accion", length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String accion; // CREAR, LEER, ACTUALIZAR, ELIMINAR, APROBAR

    // Relación con RolPermiso
    @OneToMany(mappedBy = "permiso", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RolPermiso> rolPermisos = new HashSet<>();

    /**
     * Constructor de conveniencia
     */
    public Permiso(String nombreClave, String descripcion, String modulo, String recurso, String accion) {
        this.nombreClave = nombreClave;
        this.descripcion = descripcion;
        this.modulo = modulo;
        this.recurso = recurso;
        this.accion = accion;
    }

    /**
     * Genera el nombre clave automáticamente si no está definido
     * Formato: MODULO_ACCION_RECURSO (ej: ACADEMICO_EDITAR_NOTAS)
     */
    @PrePersist
    @PreUpdate
    public void generarNombreClave() {
        if (nombreClave == null && modulo != null && accion != null && recurso != null) {
            this.nombreClave = String.format("%s_%s_%s", modulo, accion, recurso)
                    .toUpperCase()
                    .replaceAll("\\s+", "_");
        }
    }
}
