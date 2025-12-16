package upeu.edu.pe.permissions.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.permissions.application.dto.PermissionDTOs.*;
import upeu.edu.pe.permissions.domain.services.PermissionAdminService;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

/**
 * Controller para administración de roles y permisos.
 * Solo accesible por SUPERADMIN.
 */
@Path("/api/v1/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Permission Admin", description = "Administración de roles y permisos del sistema")
public class PermissionAdminController {

    @Inject
    PermissionAdminService permissionAdminService;

    // ==============================
    // ROLES
    // ==============================

    @GET
    @Path("/roles")
    @Operation(summary = "Listar roles", description = "Lista todos los roles con conteo de permisos")
    public Response getAllRoles() {
        List<RolSummaryDTO> roles = permissionAdminService.getAllRolesWithPermissionCount();
        return Response.ok(ApiResponse.success("Roles obtenidos", roles)).build();
    }

    @GET
    @Path("/roles/{rol}/permisos")
    @Operation(summary = "Permisos de un rol", description = "Obtiene los permisos detallados de un rol agrupados por módulo")
    public Response getRolPermisos(@PathParam("rol") String rol) {
        RolPermisosDetalleDTO permisos = permissionAdminService.getRolPermisos(rol);
        return Response.ok(ApiResponse.success("Permisos del rol", permisos)).build();
    }

    @PUT
    @Path("/roles/{rol}/permisos")
    @Operation(summary = "Actualizar permisos de rol", description = "Actualiza todos los permisos de un rol")
    public Response updateRolPermisos(
            @PathParam("rol") String rol,
            UpdateRolPermisosRequest request) {
        permissionAdminService.updateRolPermisos(rol, request.menuItemIdsActivos());
        return Response.ok(ApiResponse.success("Permisos actualizados")).build();
    }

    // ==============================
    // MÓDULOS
    // ==============================

    @GET
    @Path("/modulos")
    @Operation(summary = "Listar módulos", description = "Lista todos los módulos del sistema con sus targets")
    public Response getAllModulos() {
        List<ModuloDTO> modulos = permissionAdminService.getAllModulos();
        return Response.ok(ApiResponse.success("Módulos obtenidos", modulos)).build();
    }

    // ==============================
    // ASIGNACIÓN DE MÓDULOS A ROLES
    // ==============================

    @POST
    @Path("/roles/{rol}/modulo/{moduloId}")
    @Operation(summary = "Asignar módulo a rol", description = "Asigna todos los targets de un módulo a un rol")
    public Response assignModuloToRol(
            @PathParam("rol") String rol,
            @PathParam("moduloId") Long moduloId) {
        permissionAdminService.assignModuloToRol(rol, moduloId);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Módulo asignado al rol"))
                .build();
    }

    @DELETE
    @Path("/roles/{rol}/modulo/{moduloId}")
    @Operation(summary = "Quitar módulo de rol", description = "Quita todos los targets de un módulo de un rol")
    public Response removeModuloFromRol(
            @PathParam("rol") String rol,
            @PathParam("moduloId") Long moduloId) {
        permissionAdminService.removeModuloFromRol(rol, moduloId);
        return Response.ok(ApiResponse.success("Módulo removido del rol")).build();
    }
}
