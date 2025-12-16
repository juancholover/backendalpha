package upeu.edu.pe.permissions.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.permissions.domain.entities.RolMenu;

import java.util.List;

/**
 * Repository para RolMenu.
 */
@ApplicationScoped
public class RolMenuRepository implements PanacheRepository<RolMenu> {

    public List<RolMenu> findByRolNombre(String rolNombre) {
        return list("rolNombre = ?1 AND active = true", rolNombre);
    }

    public boolean existsByRolAndMenu(String rolNombre, Long menuItemId) {
        return count("rolNombre = ?1 AND menuItem.id = ?2 AND active = true", rolNombre, menuItemId) > 0;
    }

    public boolean existsByRolAndMenuItem(String rolNombre, Long menuItemId) {
        return existsByRolAndMenu(rolNombre, menuItemId);
    }

    public void deleteByRolNombre(String rolNombre) {
        delete("rolNombre = ?1", rolNombre);
    }

    public void deleteByRolAndMenuItem(String rolNombre, Long menuItemId) {
        delete("rolNombre = ?1 AND menuItem.id = ?2", rolNombre, menuItemId);
    }
}
