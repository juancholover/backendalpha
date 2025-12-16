package upeu.edu.pe.permissions.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.permissions.application.dto.PermissionsResponseDTO;
import upeu.edu.pe.permissions.application.dto.PermissionsResponseDTO.*;
import upeu.edu.pe.permissions.domain.entities.*;
import upeu.edu.pe.permissions.domain.repositories.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio simplificado que construye la estructura de permisos para un
 * usuario.
 * Utiliza el nuevo esquema: menu_item (jerárquico) + api_permiso + rol_menu
 */
@ApplicationScoped
public class PermissionsService {

    @Inject
    MenuItemRepository menuItemRepository;

    @Inject
    ApiPermisoRepository apiPermisoRepository;

    /**
     * Construye la estructura completa de permisos para un usuario.
     */
    public PermissionsResponseDTO buildPermissionsForUser(String email, List<String> roles) {
        PermissionsResponseDTO permissions = new PermissionsResponseDTO();

        // 1. Obtener islas por roles + islas individuales del usuario
        Set<MenuItem> todasLasIslas = new LinkedHashSet<>();

        for (String rol : roles) {
            todasLasIslas.addAll(menuItemRepository.findIslasByRolNombre(rol));
        }

        // Agregar islas individuales del usuario
        todasLasIslas.addAll(menuItemRepository.findIslasByUsuarioEmail(email));

        // 2. Construir lista de IslaDTO con sidebarTargets
        List<IslaDTO> islasDTO = new ArrayList<>();

        for (MenuItem isla : todasLasIslas) {
            IslaDTO islaDTO = buildIslaDTO(isla, roles, email);
            islasDTO.add(islaDTO);
        }

        // Ordenar por orden
        islasDTO.sort(Comparator.comparingInt(i -> i.getOrden() != null ? i.getOrden() : 999));

        permissions.setIslas(islasDTO);

        // 3. Calcular metadata
        MetadataPermisosDTO metadata = buildMetadata(islasDTO);
        permissions.setMetadata(metadata);

        // 4. Permisos no usados ahora pero mantenemos estructura
        permissions.setPermisos(new HashMap<>());
        permissions.setPermisosIndividuales(new ArrayList<>());

        return permissions;
    }

    private IslaDTO buildIslaDTO(MenuItem isla, List<String> roles, String email) {
        IslaDTO dto = new IslaDTO();
        dto.setId("isla_" + isla.getId());
        dto.setCodigo(isla.getCodigo());
        dto.setNombre(isla.getNombre());
        dto.setDescripcion(isla.getDescripcion());
        dto.setIcono(isla.getIcono());
        dto.setColor(isla.getColor());
        dto.setRutaDefault(isla.getRutaFrontend());
        dto.setEsIslaPrincipal(isla.getOrden() != null && isla.getOrden() == 1);
        dto.setOrden(isla.getOrden());

        // Obtener sidebar-targets de esta isla
        List<SidebarTargetDTO> sidebarTargets = new ArrayList<>();

        for (String rol : roles) {
            List<MenuItem> targets = menuItemRepository.findSidebarTargetsByIslaAndRol(isla.getId(), rol);
            for (MenuItem target : targets) {
                if (sidebarTargets.stream().noneMatch(t -> t.getCodigo().equals(target.getCodigo()))) {
                    sidebarTargets.add(buildSidebarTargetDTO(target));
                }
            }
        }

        sidebarTargets.sort(Comparator.comparingInt(t -> t.getOrden() != null ? t.getOrden() : 999));
        dto.setSidebarTargets(sidebarTargets);

        return dto;
    }

    private SidebarTargetDTO buildSidebarTargetDTO(MenuItem target) {
        SidebarTargetDTO dto = new SidebarTargetDTO();
        dto.setId("target_" + target.getId());
        dto.setCodigo(target.getCodigo());
        dto.setNombre(target.getNombre());
        dto.setDescripcion(target.getDescripcion());
        dto.setRutaFrontend(target.getRutaFrontend());
        dto.setIcono(target.getIcono());
        dto.setOrden(target.getOrden());

        // Obtener APIs de este sidebar-target
        List<ApiPermiso> apis = apiPermisoRepository.findByMenuItemId(target.getId());
        List<ApiPermisoDTO> apisDTO = apis.stream()
                .map(this::buildApiPermisoDTO)
                .collect(Collectors.toList());

        dto.setApis(apisDTO);

        return dto;
    }

    private ApiPermisoDTO buildApiPermisoDTO(ApiPermiso api) {
        ApiPermisoDTO dto = new ApiPermisoDTO();
        dto.setApiBase(api.getApiBase());
        dto.setDescripcion(api.getDescripcion());
        dto.setGet(Boolean.TRUE.equals(api.getPuedeGet()));
        dto.setPost(Boolean.TRUE.equals(api.getPuedePost()));
        dto.setPut(Boolean.TRUE.equals(api.getPuedePut()));
        dto.setDelete(Boolean.TRUE.equals(api.getPuedeDelete()));
        return dto;
    }

    private MetadataPermisosDTO buildMetadata(List<IslaDTO> islas) {
        MetadataPermisosDTO metadata = new MetadataPermisosDTO();
        metadata.setTotalIslas(islas.size());

        int totalSidebarTargets = 0;
        int totalApis = 0;

        for (IslaDTO isla : islas) {
            if (isla.getSidebarTargets() != null) {
                totalSidebarTargets += isla.getSidebarTargets().size();
                for (SidebarTargetDTO target : isla.getSidebarTargets()) {
                    if (target.getApis() != null) {
                        totalApis += target.getApis().size();
                    }
                }
            }
        }

        metadata.setTotalModulos(totalSidebarTargets);
        metadata.setTotalRecursos(totalApis);
        metadata.setTotalPermisosActivos(totalApis);
        metadata.setPermisosIndividualesCount(0);

        // Encontrar isla principal
        islas.stream()
                .filter(i -> Boolean.TRUE.equals(i.getEsIslaPrincipal()))
                .findFirst()
                .ifPresent(i -> metadata.setIslaPrincipal(i.getCodigo()));

        return metadata;
    }
}
