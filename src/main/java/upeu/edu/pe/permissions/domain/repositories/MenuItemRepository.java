package upeu.edu.pe.permissions.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.permissions.domain.entities.MenuItem;

import java.util.List;
import java.util.Optional;

/**
 * Repository para MenuItem (Islas y Sidebar-Targets).
 */
@ApplicationScoped
public class MenuItemRepository implements PanacheRepository<MenuItem> {

    /**
     * Obtener todas las ISLAS (id_padre = NULL)
     */
    public List<MenuItem> findAllIslas() {
        return list("padre IS NULL AND active = true ORDER BY orden");
    }

    /**
     * Obtener sidebar-targets de una isla
     */
    public List<MenuItem> findSidebarTargetsByIslaId(Long islaId) {
        return list("padre.id = ?1 AND active = true ORDER BY orden", islaId);
    }

    /**
     * Obtener menú por código
     */
    public Optional<MenuItem> findByCodigo(String codigo) {
        return find("codigo = ?1 AND active = true", codigo).firstResultOptional();
    }

    /**
     * Obtener islas asignadas a un rol
     */
    public List<MenuItem> findIslasByRolNombre(String rolNombre) {
        return getEntityManager()
                .createQuery("""
                        SELECT DISTINCT m FROM MenuItem m
                        JOIN RolMenu rm ON rm.menuItem = m
                        WHERE rm.rolNombre = :rol
                        AND m.padre IS NULL
                        AND m.active = true
                        AND rm.active = true
                        ORDER BY m.orden
                        """, MenuItem.class)
                .setParameter("rol", rolNombre)
                .getResultList();
    }

    /**
     * Obtener sidebar-targets de una isla asignados a un rol
     */
    public List<MenuItem> findSidebarTargetsByIslaAndRol(Long islaId, String rolNombre) {
        return getEntityManager()
                .createQuery("""
                        SELECT DISTINCT m FROM MenuItem m
                        JOIN RolMenu rm ON rm.menuItem = m
                        WHERE m.padre.id = :islaId
                        AND rm.rolNombre = :rol
                        AND m.active = true
                        AND rm.active = true
                        ORDER BY m.orden
                        """, MenuItem.class)
                .setParameter("islaId", islaId)
                .setParameter("rol", rolNombre)
                .getResultList();
    }

    /**
     * Obtener islas individuales de un usuario
     */
    public List<MenuItem> findIslasByUsuarioEmail(String email) {
        return getEntityManager()
                .createQuery("""
                        SELECT DISTINCT m FROM MenuItem m
                        JOIN UsuarioMenu um ON um.menuItem = m
                        WHERE um.usuarioEmail = :email
                        AND m.padre IS NULL
                        AND m.active = true
                        AND um.active = true
                        AND (um.fechaExpiracion IS NULL OR um.fechaExpiracion > CURRENT_TIMESTAMP)
                        ORDER BY m.orden
                        """, MenuItem.class)
                .setParameter("email", email)
                .getResultList();
    }

    /**
     * Obtener todos los módulos (alias de findAllIslas)
     */
    public List<MenuItem> findModulos() {
        return findAllIslas();
    }

    /**
     * Obtener hijos (targets) de un módulo padre
     */
    public List<MenuItem> findHijosByPadreId(Long padreId) {
        return findSidebarTargetsByIslaId(padreId);
    }
}
