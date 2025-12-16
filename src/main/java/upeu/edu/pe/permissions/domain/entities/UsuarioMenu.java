package upeu.edu.pe.permissions.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;

import java.time.LocalDateTime;

/**
 * UsuarioMenu - Permisos individuales de menú para usuarios específicos.
 */
@Entity
@Table(name = "usuario_menu", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "usuario_email", "menu_item_id" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class UsuarioMenu extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "usuario_email", nullable = false, length = 255)
    private String usuarioEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(name = "otorgado_por", length = 255)
    private String otorgadoPor;

    @Column(name = "fecha_otorgamiento")
    private LocalDateTime fechaOtorgamiento;

    @Column(name = "fecha_expiracion")
    private LocalDateTime fechaExpiracion;

    @Column(name = "razon", columnDefinition = "TEXT")
    private String razon;

    /**
     * ¿El permiso está vigente?
     */
    public boolean estaVigente() {
        if (fechaExpiracion == null)
            return true;
        return LocalDateTime.now().isBefore(fechaExpiracion);
    }
}
