package upeu.edu.pe.permissions.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;

/**
 * RolMenu - Asignación de menús (islas y sidebar-targets) a roles.
 */
@Entity
@Table(name = "rol_menu", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "rol_nombre", "menu_item_id" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class RolMenu extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "rol_nombre", nullable = false, length = 50)
    private String rolNombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;
}
