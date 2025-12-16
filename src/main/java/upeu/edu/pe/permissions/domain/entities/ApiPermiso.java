package upeu.edu.pe.permissions.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;

/**
 * ApiPermiso - Permisos de API con columnas booleanas para cada método HTTP.
 * Asociado a un MenuItem (sidebar-target).
 */
@Entity
@Table(name = "api_permiso", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "menu_item_id", "api_base" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class ApiPermiso extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(name = "api_base", nullable = false, length = 255)
    private String apiBase;

    @Column(name = "descripcion", nullable = false, length = 255)
    private String descripcion;

    @Column(name = "puede_get")
    private Boolean puedeGet = false;

    @Column(name = "puede_post")
    private Boolean puedePost = false;

    @Column(name = "puede_put")
    private Boolean puedePut = false;

    @Column(name = "puede_delete")
    private Boolean puedeDelete = false;
}
