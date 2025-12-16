package upeu.edu.pe.permissions.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.permissions.domain.entities.MenuItem;
import upeu.edu.pe.permissions.domain.entities.ApiPermiso;
import upeu.edu.pe.permissions.domain.repositories.MenuItemRepository;
import upeu.edu.pe.permissions.domain.repositories.ApiPermisoRepository;
import upeu.edu.pe.shared.response.ApiResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller para gestión de menús (islas y sidebar-targets) y sus APIs.
 */
@Path("/api/v1/menu")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "01. Administración - Menú", description = "Gestión de islas, sidebar-targets y permisos de API")
public class MenuController {

    @Inject
    MenuItemRepository menuItemRepository;

    @Inject
    ApiPermisoRepository apiPermisoRepository;

    // =====================================================
    // ISLAS
    // =====================================================

    @GET
    @Path("/islas")
    @Operation(summary = "Listar todas las islas")
    public Response findAllIslas() {
        List<MenuItem> islas = menuItemRepository.findAllIslas();
        return Response.ok(ApiResponse.success("Islas obtenidas", islas)).build();
    }

    @GET
    @Path("/islas/{id}")
    @Operation(summary = "Obtener isla por ID")
    public Response findIslaById(@PathParam("id") Long id) {
        MenuItem isla = menuItemRepository.findById(id);
        if (isla == null || !isla.esIsla()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiResponse.error("Isla no encontrada"))
                    .build();
        }
        return Response.ok(ApiResponse.success("Isla obtenida", isla)).build();
    }

    @POST
    @Path("/islas")
    @Transactional
    @Operation(summary = "Crear nueva isla")
    public Response createIsla(MenuItem isla) {
        isla.setPadre(null); // Es una isla
        isla.setCreatedAt(LocalDateTime.now());
        isla.setUpdatedAt(LocalDateTime.now());
        isla.setActive(true);
        menuItemRepository.persist(isla);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Isla creada", isla))
                .build();
    }

    // =====================================================
    // SIDEBAR-TARGETS
    // =====================================================

    @GET
    @Path("/islas/{islaId}/sidebar-targets")
    @Operation(summary = "Listar sidebar-targets de una isla")
    public Response findSidebarTargets(@PathParam("islaId") Long islaId) {
        List<MenuItem> targets = menuItemRepository.findSidebarTargetsByIslaId(islaId);
        return Response.ok(ApiResponse.success("Sidebar-targets obtenidos", targets)).build();
    }

    @POST
    @Path("/islas/{islaId}/sidebar-targets")
    @Transactional
    @Operation(summary = "Crear sidebar-target para una isla")
    public Response createSidebarTarget(@PathParam("islaId") Long islaId, MenuItem target) {
        MenuItem isla = menuItemRepository.findById(islaId);
        if (isla == null || !isla.esIsla()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("Isla no encontrada"))
                    .build();
        }

        target.setPadre(isla);
        target.setCreatedAt(LocalDateTime.now());
        target.setUpdatedAt(LocalDateTime.now());
        target.setActive(true);
        menuItemRepository.persist(target);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Sidebar-target creado", target))
                .build();
    }

    // =====================================================
    // API PERMISOS
    // =====================================================

    @GET
    @Path("/sidebar-targets/{targetId}/apis")
    @Operation(summary = "Listar APIs de un sidebar-target")
    public Response findApis(@PathParam("targetId") Long targetId) {
        List<ApiPermiso> apis = apiPermisoRepository.findByMenuItemId(targetId);
        return Response.ok(ApiResponse.success("APIs obtenidas", apis)).build();
    }

    @POST
    @Path("/sidebar-targets/{targetId}/apis")
    @Transactional
    @Operation(summary = "Crear API para un sidebar-target")
    public Response createApi(@PathParam("targetId") Long targetId, ApiPermiso api) {
        MenuItem target = menuItemRepository.findById(targetId);
        if (target == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("Sidebar-target no encontrado"))
                    .build();
        }

        api.setMenuItem(target);
        api.setCreatedAt(LocalDateTime.now());
        api.setUpdatedAt(LocalDateTime.now());
        api.setActive(true);
        apiPermisoRepository.persist(api);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("API creada", api))
                .build();
    }

    @PUT
    @Path("/apis/{id}")
    @Transactional
    @Operation(summary = "Actualizar permisos de API")
    public Response updateApi(@PathParam("id") Long id, ApiPermiso updatedApi) {
        ApiPermiso api = apiPermisoRepository.findById(id);
        if (api == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiResponse.error("API no encontrada"))
                    .build();
        }

        api.setApiBase(updatedApi.getApiBase());
        api.setDescripcion(updatedApi.getDescripcion());
        api.setPuedeGet(updatedApi.getPuedeGet());
        api.setPuedePost(updatedApi.getPuedePost());
        api.setPuedePut(updatedApi.getPuedePut());
        api.setPuedeDelete(updatedApi.getPuedeDelete());
        api.setUpdatedAt(LocalDateTime.now());

        return Response.ok(ApiResponse.success("API actualizada", api)).build();
    }
}
