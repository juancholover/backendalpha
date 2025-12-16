package upeu.edu.pe.permissions.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.permissions.application.dto.PermissionDTOs.*;
import upeu.edu.pe.permissions.domain.services.UserPermissionService;
import upeu.edu.pe.shared.response.ApiResponse;
import upeu.edu.pe.shared.context.AuditContext;

import java.util.List;

/**
 * Controller para administración de permisos individuales de usuarios.
 * Solo accesible por SUPERADMIN y ADMIN.
 */
@Path("/api/v1/admin/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "User Permissions", description = "Gestión de permisos individuales de usuarios")
public class UserPermissionController {

    @Inject
    UserPermissionService userPermissionService;

    @Inject
    AuditContext auditContext;

    @GET
    @Operation(summary = "Buscar usuarios", description = "Busca usuarios por nombre, email o teléfono")
    public Response searchUsuarios(@QueryParam("q") String query) {
        if (query == null || query.length() < 2) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("El término de búsqueda debe tener al menos 2 caracteres"))
                    .build();
        }
        List<UsuarioPermisosDTO> usuarios = userPermissionService.searchUsuarios(query);
        return Response.ok(ApiResponse.success("Usuarios encontrados", usuarios)).build();
    }

    @GET
    @Path("/{email}/permisos")
    @Operation(summary = "Permisos de usuario", description = "Obtiene los permisos de un usuario específico")
    public Response getUsuarioPermisos(@PathParam("email") String email) {
        UsuarioPermisosDTO permisos = userPermissionService.getUsuarioPermisos(email);
        return Response.ok(ApiResponse.success("Permisos del usuario", permisos)).build();
    }

    @POST
    @Path("/{email}/permiso")
    @Operation(summary = "Asignar permiso individual", description = "Asigna un permiso específico a un usuario")
    public Response assignPermiso(
            @PathParam("email") String email,
            AsignarPermisoIndividualRequest request) {
        try {
            String otorgadoPor = auditContext.getCurrentUser();
            userPermissionService.assignPermisoIndividual(
                    email,
                    request.menuItemId(),
                    request.razon(),
                    otorgadoPor);
            return Response.status(Response.Status.CREATED)
                    .entity(ApiResponse.success("Permiso asignado"))
                    .build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        }
    }

    @DELETE
    @Path("/{email}/permiso/{permisoId}")
    @Operation(summary = "Quitar permiso individual", description = "Quita un permiso específico de un usuario")
    public Response removePermiso(
            @PathParam("email") String email,
            @PathParam("permisoId") Long permisoId) {
        userPermissionService.removePermisoIndividual(permisoId);
        return Response.ok(ApiResponse.success("Permiso removido")).build();
    }

    @POST
    @Path("/{email}/modulo/{moduloId}")
    @Operation(summary = "Asignar módulo completo", description = "Asigna todos los targets de un módulo a un usuario")
    public Response assignModulo(
            @PathParam("email") String email,
            @PathParam("moduloId") Long moduloId,
            AsignarPermisoIndividualRequest request) {
        String otorgadoPor = auditContext.getCurrentUser();
        userPermissionService.assignModuloToUsuario(
                email,
                moduloId,
                request != null ? request.razon() : null,
                otorgadoPor);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Módulo asignado al usuario"))
                .build();
    }
}
