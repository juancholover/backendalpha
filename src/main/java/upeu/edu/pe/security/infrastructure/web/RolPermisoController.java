package upeu.edu.pe.security.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import upeu.edu.pe.security.application.dto.RolPermisoRequestDTO;
import upeu.edu.pe.security.application.dto.RolPermisoResponseDTO;
import upeu.edu.pe.security.domain.services.RolPermisoService;

import java.util.List;

@Path("/api/v1/roles-permisos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RolPermisoController {

    @Inject
    RolPermisoService rolPermisoService;

    @GET
    @Path("/rol/{rolId}")
    public Response findByRol(@PathParam("rolId") Long rolId) {
        List<RolPermisoResponseDTO> permisos = rolPermisoService.findByRol(rolId);
        return Response.ok(permisos).build();
    }

    @GET
    @Path("/permiso/{permisoId}")
    public Response findByPermiso(@PathParam("permisoId") Long permisoId) {
        List<RolPermisoResponseDTO> roles = rolPermisoService.findByPermiso(permisoId);
        return Response.ok(roles).build();
    }

    @GET
    @Path("/rol/{rolId}/delegables")
    public Response findDelegablesByRol(@PathParam("rolId") Long rolId) {
        List<RolPermisoResponseDTO> permisos = rolPermisoService.findDelegablesByRol(rolId);
        return Response.ok(permisos).build();
    }

    @POST
    @Path("/rol/{rolId}/permiso/{permisoId}")
    public Response assignPermiso(
            @PathParam("rolId") Long rolId,
            @PathParam("permisoId") Long permisoId,
            @QueryParam("puedeDeleagar") @DefaultValue("false") Boolean puedeDeleagar,
            @QueryParam("restriccion") String restriccion) {
        RolPermisoRequestDTO requestDTO = new RolPermisoRequestDTO();
        requestDTO.setRolId(rolId);
        requestDTO.setPermisoId(permisoId);
        requestDTO.setPuedeDeleagar(puedeDeleagar);
        requestDTO.setRestriccion(restriccion);
        RolPermisoResponseDTO resultado = rolPermisoService.assignPermiso(requestDTO);
        return Response.status(Response.Status.CREATED).entity(resultado).build();
    }

    @DELETE
    @Path("/rol/{rolId}/permiso/{permisoId}")
    public Response removePermiso(
            @PathParam("rolId") Long rolId,
            @PathParam("permisoId") Long permisoId) {
        rolPermisoService.removePermiso(rolId, permisoId);
        return Response.noContent().build();
    }

    @PUT
    @Path("/rol/{rolId}/permiso/{permisoId}/restriccion")
    public Response updateRestriccion(
            @PathParam("rolId") Long rolId,
            @PathParam("permisoId") Long permisoId,
            String restriccion) {
        RolPermisoResponseDTO resultado = rolPermisoService.updateRestriccion(
                rolId, permisoId, restriccion);
        return Response.ok(resultado).build();
    }

    @POST
    @Path("/rol/{rolId}/permisos/bulk")
    public Response assignMultiplePermisos(
            @PathParam("rolId") Long rolId,
            List<Long> permisoIds) {
        rolPermisoService.assignMultiplePermisos(rolId, permisoIds);
        return Response.status(Response.Status.CREATED).build();
    }

    @DELETE
    @Path("/rol/{rolId}/permisos/bulk")
    public Response removeMultiplePermisos(
            @PathParam("rolId") Long rolId,
            List<Long> permisoIds) {
        rolPermisoService.removeMultiplePermisos(rolId, permisoIds);
        return Response.noContent().build();
    }

    @GET
    @Path("/existe")
    public Response existsByRolAndPermiso(
            @QueryParam("rolId") Long rolId,
            @QueryParam("permisoId") Long permisoId) {
        boolean existe = rolPermisoService.existsByRolAndPermiso(rolId, permisoId);
        return Response.ok().entity("{\"existe\": " + existe + "}").build();
    }
}
