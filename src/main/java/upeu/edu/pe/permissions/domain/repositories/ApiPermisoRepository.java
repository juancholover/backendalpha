package upeu.edu.pe.permissions.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.permissions.domain.entities.ApiPermiso;

import java.util.List;

/**
 * Repository para ApiPermiso.
 */
@ApplicationScoped
public class ApiPermisoRepository implements PanacheRepository<ApiPermiso> {

    /**
     * Obtener APIs por MenuItem (sidebar-target)
     */
    public List<ApiPermiso> findByMenuItemId(Long menuItemId) {
        return list("menuItem.id = ?1 AND active = true", menuItemId);
    }

    /**
     * Obtener todas las APIs activas
     */
    public List<ApiPermiso> findAllActive() {
        return list("active = true");
    }
}
