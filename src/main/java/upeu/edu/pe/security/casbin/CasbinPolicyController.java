package upeu.edu.pe.security.casbin;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.shared.response.ApiResponse;
import upeu.edu.pe.security.domain.services.RoleSyncService;
import upeu.edu.pe.security.domain.repositories.AuthUsuarioRepository;
import upeu.edu.pe.security.domain.entities.AuthUsuario;

import java.util.List;

/**
 * REST Controller for managing Casbin policies.
 * Only accessible by ADMIN role.
 */
@Path("/api/v1/casbin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Casbin Policies", description = "Gestión de políticas de autorización")
public class CasbinPolicyController {

    @Inject
    CasbinPolicyService policyService;

    @Inject
    RoleSyncService roleSyncService;

    @Inject
    AuthUsuarioRepository authUsuarioRepository;

    // ==============================
    // POLICY MANAGEMENT
    // ==============================

    @POST
    @Path("/policies")
    @Operation(summary = "Agregar política", description = "Agrega una política de permiso para un rol")
    public Response addPolicy(PolicyRequest request) {
        boolean added = policyService.addPolicy(request.role, request.path, request.action);
        if (added) {
            return Response.status(Response.Status.CREATED)
                    .entity(ApiResponse.success("Política agregada", request))
                    .build();
        }
        return Response.status(Response.Status.CONFLICT)
                .entity(ApiResponse.error("La política ya existe"))
                .build();
    }

    @DELETE
    @Path("/policies")
    @Operation(summary = "Eliminar política", description = "Elimina una política de permiso")
    public Response removePolicy(
            @QueryParam("role") String role,
            @QueryParam("path") String path,
            @QueryParam("action") String action) {
        boolean removed = policyService.removePolicy(role, path, action);
        if (removed) {
            return Response.ok(ApiResponse.success("Política eliminada", null)).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(ApiResponse.error("Política no encontrada"))
                .build();
    }

    // ==============================
    // ROLE ASSIGNMENT
    // ==============================

    @POST
    @Path("/roles/assign")
    @Operation(summary = "Asignar rol a usuario", description = "Asigna un rol a un usuario por su email")
    public Response assignRole(RoleAssignmentRequest request) {
        boolean assigned = policyService.assignRole(request.userEmail, request.role);
        if (assigned) {
            return Response.status(Response.Status.CREATED)
                    .entity(ApiResponse.success("Rol asignado", request))
                    .build();
        }
        return Response.status(Response.Status.CONFLICT)
                .entity(ApiResponse.error("El usuario ya tiene este rol"))
                .build();
    }

    @DELETE
    @Path("/roles/remove")
    @Operation(summary = "Remover rol de usuario", description = "Remueve un rol de un usuario")
    public Response removeRole(
            @QueryParam("userEmail") String userEmail,
            @QueryParam("role") String role) {
        boolean removed = policyService.removeRole(userEmail, role);
        if (removed) {
            return Response.ok(ApiResponse.success("Rol removido", null)).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(ApiResponse.error("Asignación no encontrada"))
                .build();
    }

    @GET
    @Path("/roles/user/{email}")
    @Operation(summary = "Obtener roles de usuario", description = "Lista todos los roles asignados a un usuario")
    public Response getUserRoles(@PathParam("email") String email) {
        List<String> roles = policyService.getUserRoles(email);
        return Response.ok(ApiResponse.success("Roles del usuario", roles)).build();
    }

    // ==============================
    // UTILITIES
    // ==============================

    @POST
    @Path("/reload")
    @Operation(summary = "Recargar políticas", description = "Recarga las políticas desde la base de datos")
    public Response reloadPolicies() {
        policyService.reloadPolicies();
        return Response.ok(ApiResponse.success("Políticas recargadas", null)).build();
    }

    @GET
    @Path("/check")
    @Operation(summary = "Verificar permiso", description = "Verifica si un usuario tiene un permiso específico")
    public Response checkPermission(
            @QueryParam("user") String user,
            @QueryParam("path") String path,
            @QueryParam("action") String action) {
        boolean allowed = policyService.hasPermission(user, path, action);
        return Response.ok(ApiResponse.success("Verificación de permiso", allowed)).build();
    }

    // ==============================
    // SYNC OPERATIONS
    // ==============================

    @POST
    @Path("/sync-all-users")
    @Operation(summary = "Sincronizar usuarios masivamente", description = "Sincroniza roles de Casbin para TODOS los usuarios existentes")
    public Response syncAllUsers() {
        List<AuthUsuario> usuarios = authUsuarioRepository.listAll();
        int count = 0;
        for (AuthUsuario u : usuarios) {
            if (u.getPersona() != null) {
                try {
                    roleSyncService.syncAllRolesForPersona(u.getPersona());
                    count++;
                } catch (Exception e) {
                    System.err.println("Error syncing user " + u.getId() + ": " + e.getMessage());
                }
            }
        }
        return Response.ok(ApiResponse.success("Roles sincronizados para " + count + " usuarios")).build();
    }

    // ==============================
    // DTOs
    // ==============================

    public static class PolicyRequest {
        public String role;
        public String path;
        public String action;
    }

    public static class RoleAssignmentRequest {
        public String userEmail;
        public String role;
    }
}
