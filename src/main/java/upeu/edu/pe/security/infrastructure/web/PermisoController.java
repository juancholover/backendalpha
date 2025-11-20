package upeu.edu.pe.security.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import upeu.edu.pe.security.application.dto.PermisoRequestDTO;
import upeu.edu.pe.security.application.dto.PermisoResponseDTO;
import upeu.edu.pe.security.domain.services.PermisoService;

import java.util.List;

@Path("/api/v1/permisos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PermisoController {

    @Inject
    PermisoService permisoService;

    @GET
    public Response findAll() {
        List<PermisoResponseDTO> permisos = permisoService.findAll();
        return Response.ok(permisos).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        PermisoResponseDTO permiso = permisoService.findById(id);
        return Response.ok(permiso).build();
    }

    @GET
    @Path("/modulo/{modulo}")
    public Response findByModulo(@PathParam("modulo") String modulo) {
        List<PermisoResponseDTO> permisos = permisoService.findByModulo(modulo);
        return Response.ok(permisos).build();
    }

    @GET
    @Path("/recurso/{recurso}")
    public Response findByRecurso(@PathParam("recurso") String recurso) {
        List<PermisoResponseDTO> permisos = permisoService.findByRecurso(recurso);
        return Response.ok(permisos).build();
    }

    @GET
    @Path("/accion/{accion}")
    public Response findByAccion(@PathParam("accion") String accion) {
        List<PermisoResponseDTO> permisos = permisoService.findByAccion(accion);
        return Response.ok(permisos).build();
    }

    @GET
    @Path("/rol/{rolId}")
    public Response findByRol(@PathParam("rolId") Long rolId) {
        List<PermisoResponseDTO> permisos = permisoService.findByRol(rolId);
        return Response.ok(permisos).build();
    }

    @GET
    @Path("/search")
    public Response search(@QueryParam("query") String query) {
        List<PermisoResponseDTO> permisos = permisoService.search(query);
        return Response.ok(permisos).build();
    }

    @POST
    public Response create(@Valid PermisoRequestDTO requestDTO) {
        PermisoResponseDTO permiso = permisoService.create(requestDTO);
        return Response.status(Response.Status.CREATED).entity(permiso).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid PermisoRequestDTO requestDTO) {
        PermisoResponseDTO permiso = permisoService.update(id, requestDTO);
        return Response.ok(permiso).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        permisoService.delete(id);
        return Response.noContent().build();
    }
}
