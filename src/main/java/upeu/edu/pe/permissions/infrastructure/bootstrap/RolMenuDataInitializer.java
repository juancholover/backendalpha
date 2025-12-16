package upeu.edu.pe.permissions.infrastructure.bootstrap;

import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import upeu.edu.pe.permissions.domain.entities.MenuItem;
import upeu.edu.pe.permissions.domain.entities.RolMenu;
import upeu.edu.pe.permissions.domain.repositories.MenuItemRepository;
import upeu.edu.pe.permissions.domain.repositories.RolMenuRepository;

import java.util.List;

/**
 * Inicializa las asignaciones de menú a roles al arrancar la aplicación.
 * Priority 2 = ejecuta después de MenuItemDataInitializer (Priority 1)
 * Solo inserta datos si la tabla está vacía.
 */
@ApplicationScoped
public class RolMenuDataInitializer {

    private static final Logger LOG = Logger.getLogger(RolMenuDataInitializer.class);

    @Inject
    MenuItemRepository menuItemRepository;

    @Inject
    RolMenuRepository rolMenuRepository;

    @Transactional
    void onStart(@Observes @Priority(2) StartupEvent ev) {
        if (rolMenuRepository.count() > 0) {
            LOG.info("RolMenu already has data, skipping initialization.");
            return;
        }

        LOG.info("Seeding RolMenu data (Rol -> Menu assignments)...");

        // ==========================================
        // SUPERADMIN: Todos los targets de SUPERADMIN
        // ==========================================
        assignAllTargetsToRol("SUPERADMIN", "SUPERADMIN");

        // ==========================================
        // ADMIN: Todos los targets de ADMIN
        // ==========================================
        assignAllTargetsToRol("ADMIN", "ADMIN");

        // ==========================================
        // PROFESOR: Todos los targets de PROFESOR
        // ==========================================
        assignAllTargetsToRol("PROFESOR", "PROFESOR");

        LOG.info("RolMenu data seeded successfully.");
    }

    /**
     * Asigna una isla y todos sus targets a un rol.
     * 
     * @param rolNombre  Nombre del rol (ej: SUPERADMIN)
     * @param islaCodigo Código de la isla (ej: SUPERADMIN)
     */
    private void assignAllTargetsToRol(String rolNombre, String islaCodigo) {
        // Buscar la isla
        MenuItem isla = menuItemRepository.find("codigo", islaCodigo).firstResult();
        if (isla == null) {
            LOG.warnf("Isla con código '%s' no encontrada, saltando asignación.", islaCodigo);
            return;
        }

        // Asignar la isla al rol
        createRolMenu(rolNombre, isla);

        // Buscar todos los targets (hijos) de esta isla
        List<MenuItem> targets = menuItemRepository.list("padre.id", isla.getId());
        for (MenuItem target : targets) {
            createRolMenu(rolNombre, target);
        }

        LOG.infof("Asignados %d items (1 isla + %d targets) al rol '%s'",
                targets.size() + 1, targets.size(), rolNombre);
    }

    private void createRolMenu(String rolNombre, MenuItem menuItem) {
        RolMenu rolMenu = new RolMenu();
        rolMenu.setRolNombre(rolNombre);
        rolMenu.setMenuItem(menuItem);
        rolMenu.setActive(true);
        rolMenuRepository.persist(rolMenu);
    }
}
