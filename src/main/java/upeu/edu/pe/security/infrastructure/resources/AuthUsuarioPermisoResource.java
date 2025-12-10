package upeu.edu.pe.security.infrastructure.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import upeu.edu.pe.security.application.dto.AuthUsuarioPermisoRequestDTO;
import upeu.edu.pe.security.application.dto.AuthUsuarioPermisoResponseDTO;
import upeu.edu.pe.security.domain.services.AuthUsuarioPermisoService;

import java.time.LocalDateTime;
import java.util.List;

@Path("/api/permisos-usuario")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthUsuarioPermisoResource {

    @Inject
    AuthUsuarioPermisoService service;

    @Context
    SecurityContext securityContext;

    /**
     * Asigna un permiso individual a un usuario
     * POST /api/permisos-usuario
     */
    @POST
    @RolesAllowed({"ADMIN", "SUPER_ADMIN"})
    public Response asignarPermiso(@Valid AuthUsuarioPermisoRequestDTO dto) {
        // Obtener el username del usuario autenticado desde el SecurityContext
        String username = securityContext.getUserPrincipal().getName();
        
        // Obtener el ID del usuario autenticado desde la base de datos
        Long asignadoPorId = service.obtenerIdUsuarioPorUsername(username);
        
        AuthUsuarioPermisoResponseDTO resultado = service.asignarPermiso(dto, asignadoPorId);
        return Response.status(Response.Status.CREATED).entity(resultado).build();
    }

    /**
     * Obtiene todos los permisos individuales de un usuario
     * GET /api/permisos-usuario/usuario/{authUsuarioId}
     */
    @GET
    @Path("/usuario/{authUsuarioId}")
    @RolesAllowed({"ADMIN", "SUPER_ADMIN"})
    public Response obtenerPermisosPorUsuario(@PathParam("authUsuarioId") Long authUsuarioId) {
        List<AuthUsuarioPermisoResponseDTO> permisos = service.obtenerPermisosPorUsuario(authUsuarioId);
        return Response.ok(permisos).build();
    }

    /**
     * Obtiene solo los permisos individuales vigentes de un usuario
     * GET /api/permisos-usuario/usuario/{authUsuarioId}/vigentes
     */
    @GET
    @Path("/usuario/{authUsuarioId}/vigentes")
    @RolesAllowed({"ADMIN", "SUPER_ADMIN"})
    public Response obtenerPermisosVigentes(@PathParam("authUsuarioId") Long authUsuarioId) {
        List<AuthUsuarioPermisoResponseDTO> permisos = service.obtenerPermisosVigentes(authUsuarioId);
        return Response.ok(permisos).build();
    }

    /**
     * Obtiene permisos individuales expirados de un usuario
     * GET /api/permisos-usuario/usuario/{authUsuarioId}/expirados
     */
    @GET
    @Path("/usuario/{authUsuarioId}/expirados")
    @RolesAllowed({"ADMIN", "SUPER_ADMIN"})
    public Response obtenerPermisosExpirados(@PathParam("authUsuarioId") Long authUsuarioId) {
        List<AuthUsuarioPermisoResponseDTO> permisos = service.obtenerPermisosExpirados(authUsuarioId);
        return Response.ok(permisos).build();
    }

    /**
     * Obtiene permisos temporales de un usuario
     * GET /api/permisos-usuario/usuario/{authUsuarioId}/temporales
     */
    @GET
    @Path("/usuario/{authUsuarioId}/temporales")
    @RolesAllowed({"ADMIN", "SUPER_ADMIN"})
    public Response obtenerPermisosTemporales(@PathParam("authUsuarioId") Long authUsuarioId) {
        List<AuthUsuarioPermisoResponseDTO> permisos = service.obtenerPermisosTemporales(authUsuarioId);
        return Response.ok(permisos).build();
    }

    /**
     * Obtiene permisos permanentes de un usuario
     * GET /api/permisos-usuario/usuario/{authUsuarioId}/permanentes
     */
    @GET
    @Path("/usuario/{authUsuarioId}/permanentes")
    @RolesAllowed({"ADMIN", "SUPER_ADMIN"})
    public Response obtenerPermisosPermanentes(@PathParam("authUsuarioId") Long authUsuarioId) {
        List<AuthUsuarioPermisoResponseDTO> permisos = service.obtenerPermisosPermanentes(authUsuarioId);
        return Response.ok(permisos).build();
    }

    /**
     * Verifica si un usuario tiene un permiso individual específico vigente
     * GET /api/permisos-usuario/usuario/{authUsuarioId}/tiene-permiso/{codigoPermiso}
     */
    @GET
    @Path("/usuario/{authUsuarioId}/tiene-permiso/{codigoPermiso}")
    @RolesAllowed({"ADMIN", "SUPER_ADMIN"})
    public Response usuarioTienePermisoVigente(
            @PathParam("authUsuarioId") Long authUsuarioId,
            @PathParam("codigoPermiso") String codigoPermiso) {
        boolean tienePermiso = service.usuarioTienePermisoVigente(authUsuarioId, codigoPermiso);
        return Response.ok()
            .entity(new PermisoCheckResponse(tienePermiso))
            .build();
    }

    /**
     * Obtiene permisos asignados por un usuario específico (auditoría)
     * GET /api/permisos-usuario/asignados-por/{asignadoPorId}
     */
    @GET
    @Path("/asignados-por/{asignadoPorId}")
    @RolesAllowed({"ADMIN", "SUPER_ADMIN"})
    public Response obtenerPermisosAsignadosPor(@PathParam("asignadoPorId") Long asignadoPorId) {
        List<AuthUsuarioPermisoResponseDTO> permisos = service.obtenerPermisosAsignadosPor(asignadoPorId);
        return Response.ok(permisos).build();
    }

    /**
     * Obtiene permisos que expiran en los próximos N días
     * GET /api/permisos-usuario/por-expirar?dias=7
     */
    @GET
    @Path("/por-expirar")
    @RolesAllowed({"ADMIN", "SUPER_ADMIN"})
    public Response obtenerPermisosPorExpirar(@QueryParam("dias") @DefaultValue("7") int dias) {
        List<AuthUsuarioPermisoResponseDTO> permisos = service.obtenerPermisosPorExpirar(dias);
        return Response.ok(permisos).build();
    }

    /**
     * Revoca un permiso individual específico de un usuario
     * DELETE /api/permisos-usuario/usuario/{authUsuarioId}/permiso/{permisoId}
     */
    @DELETE
    @Path("/usuario/{authUsuarioId}/permiso/{permisoId}")
    @RolesAllowed({"ADMIN", "SUPER_ADMIN"})
    public Response revocarPermiso(
            @PathParam("authUsuarioId") Long authUsuarioId,
            @PathParam("permisoId") Long permisoId) {
        boolean revocado = service.revocarPermiso(authUsuarioId, permisoId);
        if (revocado) {
            return Response.ok()
                .entity(new RevocacionResponse(true, "Permiso revocado exitosamente"))
                .build();
        }
        return Response.status(Response.Status.NOT_FOUND)
            .entity(new RevocacionResponse(false, "Permiso no encontrado"))
            .build();
    }

    /**
     * Revoca todos los permisos individuales de un usuario
     * DELETE /api/permisos-usuario/usuario/{authUsuarioId}/todos
     */
    @DELETE
    @Path("/usuario/{authUsuarioId}/todos")
    @RolesAllowed({"ADMIN", "SUPER_ADMIN"})
    public Response revocarTodosLosPermisos(@PathParam("authUsuarioId") Long authUsuarioId) {
        long revocados = service.revocarTodosLosPermisos(authUsuarioId);
        return Response.ok()
            .entity(new RevocacionResponse(true, revocados + " permisos revocados"))
            .build();
    }

    /**
     * Limpia permisos expirados del sistema (tarea de mantenimiento)
     * DELETE /api/permisos-usuario/limpiar-expirados
     */
    @DELETE
    @Path("/limpiar-expirados")
    @RolesAllowed({"SUPER_ADMIN"})
    public Response limpiarPermisosExpirados() {
        long eliminados = service.limpiarPermisosExpirados();
        return Response.ok()
            .entity(new LimpiezaResponse(eliminados, "Permisos expirados eliminados"))
            .build();
    }

    /**
     * Extiende la fecha de expiración de un permiso temporal
     * PUT /api/permisos-usuario/{permisoUsuarioId}/extender
     */
    @PUT
    @Path("/{permisoUsuarioId}/extender")
    @RolesAllowed({"ADMIN", "SUPER_ADMIN"})
    public Response extenderExpiracion(
            @PathParam("permisoUsuarioId") Long permisoUsuarioId,
            ExtenderExpiracionRequest request) {
        AuthUsuarioPermisoResponseDTO resultado = service.extenderExpiracion(
            permisoUsuarioId, 
            request.nuevaFechaExpiracion()
        );
        return Response.ok(resultado).build();
    }

    /**
     * Convierte un permiso temporal en permanente
     * PUT /api/permisos-usuario/{permisoUsuarioId}/convertir-permanente
     */
    @PUT
    @Path("/{permisoUsuarioId}/convertir-permanente")
    @RolesAllowed({"ADMIN", "SUPER_ADMIN"})
    public Response convertirAPermanente(@PathParam("permisoUsuarioId") Long permisoUsuarioId) {
        AuthUsuarioPermisoResponseDTO resultado = service.convertirAPermanente(permisoUsuarioId);
        return Response.ok(resultado).build();
    }

    /**
     * Cuenta permisos individuales vigentes por usuario
     * GET /api/permisos-usuario/usuario/{authUsuarioId}/contar-vigentes
     */
    @GET
    @Path("/usuario/{authUsuarioId}/contar-vigentes")
    @RolesAllowed({"ADMIN", "SUPER_ADMIN"})
    public Response contarPermisosVigentes(@PathParam("authUsuarioId") Long authUsuarioId) {
        long cantidad = service.contarPermisosVigentes(authUsuarioId);
        return Response.ok()
            .entity(new ConteoResponse(cantidad))
            .build();
    }

    // Records para respuestas internas
    public record PermisoCheckResponse(boolean tienePermiso) {}
    public record RevocacionResponse(boolean exito, String mensaje) {}
    public record LimpiezaResponse(long eliminados, String mensaje) {}
    public record ConteoResponse(long cantidad) {}
    public record ExtenderExpiracionRequest(LocalDateTime nuevaFechaExpiracion) {}
}
