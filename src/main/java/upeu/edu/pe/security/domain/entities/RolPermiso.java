package upeu.edu.pe.security.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;

@Entity
@Table(name = "rol_permiso", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"rol_id", "permiso_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class RolPermiso extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permiso_id", nullable = false)
    private Permiso permiso;

    @Column(name = "puede_delegar")
    private Boolean puedeDeleagar = false; // Si el rol puede otorgar este permiso a otros

    @Column(name = "restriccion", length = 500)
    private String restriccion; // JSON con restricciones adicionales (ej: solo su propia data)

    /**
     * Constructor de conveniencia
     */
    public RolPermiso(Rol rol, Permiso permiso) {
        this.rol = rol;
        this.permiso = permiso;
        this.puedeDeleagar = false;
    }

    /**
     * Constructor completo
     */
    public RolPermiso(Rol rol, Permiso permiso, Boolean puedeDeleagar, String restriccion) {
        this.rol = rol;
        this.permiso = permiso;
        this.puedeDeleagar = puedeDeleagar;
        this.restriccion = restriccion;
    }
}
