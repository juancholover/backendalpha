package upeu.edu.pe.permissions.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.permissions.application.dto.PermissionDTOs.*;
import upeu.edu.pe.permissions.domain.entities.MenuItem;
import upeu.edu.pe.permissions.domain.entities.RolMenu;
import upeu.edu.pe.permissions.domain.repositories.MenuItemRepository;
import upeu.edu.pe.permissions.domain.repositories.RolMenuRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para administración de permisos por rol.
 */
@ApplicationScoped
public class PermissionAdminService {

    @Inject
    MenuItemRepository menuItemRepository;

    @Inject
    RolMenuRepository rolMenuRepository;

    /**
     * Obtiene todos los roles únicos con conteo de permisos.
     */
    public List<RolSummaryDTO> getAllRolesWithPermissionCount() {
        // Obtener todos los rolMenu agrupados por rol
        List<RolMenu> allRolMenus = rolMenuRepository.listAll();

        Map<String, List<RolMenu>> byRol = allRolMenus.stream()
                .filter(rm -> rm.getActive())
                .collect(Collectors.groupingBy(RolMenu::getRolNombre));

        // Obtener todos los módulos para contar - (NO NECESARIO AQUÍ)
        // List<MenuItem> modulos = menuItemRepository.findModulos();

        List<RolSummaryDTO> result = new ArrayList<>();

        // Roles predefinidos del sistema
        List<String> rolesBase = List.of(
                "SUPERADMIN", "ADMIN", "RECTOR", "DECANO",
                "DIRECTOR_ESCUELA", "SECRETARIO_GENERAL", "PROFESOR", "ESTUDIANTE");

        for (String rol : rolesBase) {
            List<RolMenu> permisos = byRol.getOrDefault(rol, List.of());

            // Contar módulos únicos (contando por padre)
            Set<Long> modulosIds = permisos.stream()
                    .map(rm -> {
                        MenuItem mi = rm.getMenuItem();
                        return mi.getPadre() != null ? mi.getPadre().getId() : mi.getId();
                    })
                    .collect(Collectors.toSet());

            result.add(new RolSummaryDTO(rol, permisos.size(), modulosIds.size()));
        }

        return result;
    }

    /**
     * Obtiene los permisos detallados de un rol, agrupados por módulo.
     */
    public RolPermisosDetalleDTO getRolPermisos(String rolNombre) {
        // Obtener todos los módulos con sus targets
        List<MenuItem> modulos = menuItemRepository.findModulos();

        // Obtener permisos actuales del rol
        List<RolMenu> permisosRol = rolMenuRepository.findByRolNombre(rolNombre);
        Set<Long> menuItemsActivos = permisosRol.stream()
                .filter(RolMenu::getActive)
                .map(rm -> rm.getMenuItem().getId())
                .collect(Collectors.toSet());

        List<ModuloDTO> modulosDTO = new ArrayList<>();
        int totalPermisos = 0;

        for (MenuItem modulo : modulos) {
            List<TargetDTO> targetsDTO = new ArrayList<>();

            // Obtener hijos (targets) del módulo
            List<MenuItem> targets = menuItemRepository.findHijosByPadreId(modulo.getId());

            for (MenuItem target : targets) {
                boolean activo = menuItemsActivos.contains(target.getId());
                if (activo)
                    totalPermisos++;

                // Obtener APIs del target
                List<ApiPermisoDTO> apisDTO = target.getApis().stream()
                        .map(api -> new ApiPermisoDTO(
                                api.getId(),
                                api.getApiBase(),
                                api.getDescripcion(),
                                api.getPuedeGet(),
                                api.getPuedePost(),
                                api.getPuedePut(),
                                api.getPuedeDelete()))
                        .toList();

                targetsDTO.add(new TargetDTO(
                        target.getId(),
                        target.getCodigo(),
                        target.getNombre(),
                        target.getDescripcion(),
                        activo,
                        apisDTO));
            }

            modulosDTO.add(new ModuloDTO(
                    modulo.getId(),
                    modulo.getCodigo(),
                    modulo.getNombre(),
                    modulo.getDescripcion(),
                    modulo.getIcono(),
                    modulo.getColor(),
                    modulo.getOrden(),
                    targetsDTO));
        }

        return new RolPermisosDetalleDTO(rolNombre, totalPermisos, modulosDTO);
    }

    /**
     * Actualiza los permisos de un rol.
     */
    @Transactional
    public void updateRolPermisos(String rolNombre, List<Long> menuItemIdsActivos) {
        // Eliminar permisos actuales
        rolMenuRepository.deleteByRolNombre(rolNombre);

        // Crear nuevos permisos
        for (Long menuItemId : menuItemIdsActivos) {
            MenuItem menuItem = menuItemRepository.findById(menuItemId);
            if (menuItem != null) {
                RolMenu rolMenu = new RolMenu();
                rolMenu.setRolNombre(rolNombre);
                rolMenu.setMenuItem(menuItem);
                rolMenu.setActive(true);
                rolMenuRepository.persist(rolMenu);
            }
        }
    }

    /**
     * Obtiene todos los módulos con sus targets.
     */
    public List<ModuloDTO> getAllModulos() {
        List<MenuItem> modulos = menuItemRepository.findModulos();

        return modulos.stream()
                .map(modulo -> {
                    List<MenuItem> targets = menuItemRepository.findHijosByPadreId(modulo.getId());
                    List<TargetDTO> targetsDTO = targets.stream()
                            .map(target -> new TargetDTO(
                                    target.getId(),
                                    target.getCodigo(),
                                    target.getNombre(),
                                    target.getDescripcion(),
                                    true, // No aplica aquí
                                    List.of()))
                            .toList();

                    return new ModuloDTO(
                            modulo.getId(),
                            modulo.getCodigo(),
                            modulo.getNombre(),
                            modulo.getDescripcion(),
                            modulo.getIcono(),
                            modulo.getColor(),
                            modulo.getOrden(),
                            targetsDTO);
                })
                .toList();
    }

    /**
     * Asigna un módulo completo (todos sus targets) a un rol.
     */
    @Transactional
    public void assignModuloToRol(String rolNombre, Long moduloId) {
        // Obtener todos los targets del módulo
        List<MenuItem> targets = menuItemRepository.findHijosByPadreId(moduloId);

        for (MenuItem target : targets) {
            // Verificar si ya existe
            if (!rolMenuRepository.existsByRolAndMenuItem(rolNombre, target.getId())) {
                RolMenu rolMenu = new RolMenu();
                rolMenu.setRolNombre(rolNombre);
                rolMenu.setMenuItem(target);
                rolMenu.setActive(true);
                rolMenuRepository.persist(rolMenu);
            }
        }
    }

    /**
     * Quita un módulo completo de un rol.
     */
    @Transactional
    public void removeModuloFromRol(String rolNombre, Long moduloId) {
        List<MenuItem> targets = menuItemRepository.findHijosByPadreId(moduloId);

        for (MenuItem target : targets) {
            rolMenuRepository.deleteByRolAndMenuItem(rolNombre, target.getId());
        }
    }
}
