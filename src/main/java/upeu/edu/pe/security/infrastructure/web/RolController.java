package upeu.edu.pe.security.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import upeu.edu.pe.security.application.dto.RolRequestDTO;
import upeu.edu.pe.security.application.dto.RolResponseDTO;
import upeu.edu.pe.security.domain.services.RolService;

import java.util.List;

@Path("/api/v1/roles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RolController {

    @Inject
    RolService rolService;

    @GET
    public Response findAll() {
        List<RolResponseDTO> roles = rolService.findAll();
        return Response.ok(roles).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        RolResponseDTO rol = rolService.findById(id);
        return Response.ok(rol).build();
    }

    @GET
    @Path("/universidad/{universidadId}")
    public Response findByUniversidad(@PathParam("universidadId") Long universidadId) {
        List<RolResponseDTO> roles = rolService.findByUniversidad(universidadId);
        return Response.ok(roles).build();
    }

    @GET
    @Path("/sistema")
    public Response findRolesSistema() {
        List<RolResponseDTO> roles = rolService.findRolesSistema();
        return Response.ok(roles).build();
    }

    @GET
    @Path("/permiso/{permisoNombre}/universidad/{universidadId}")
    public Response findByPermisoNombre(
            @PathParam("permisoNombre") String permisoNombre,
            @PathParam("universidadId") Long universidadId) {
        List<RolResponseDTO> roles = rolService.findByPermisoNombre(permisoNombre, universidadId);
        return Response.ok(roles).build();
    }

    @POST
    public Response create(@Valid RolRequestDTO requestDTO) {
        RolResponseDTO rol = rolService.create(requestDTO);
        return Response.status(Response.Status.CREATED).entity(rol).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid RolRequestDTO requestDTO) {
        RolResponseDTO rol = rolService.update(id, requestDTO);
        return Response.ok(rol).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        rolService.delete(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/existe")
    public Response existsByNombre(
            @QueryParam("nombre") String nombre,
            @QueryParam("universidadId") Long universidadId) {
        boolean existe = rolService.existsByNombre(nombre, universidadId);
        return Response.ok().entity("{\"existe\": " + existe + "}").build();
    }

    @GET
    @Path("/{id}/usuarios/count")
    public Response countUsuariosConRol(@PathParam("id") Long id) {
        long count = rolService.countUsuariosConRol(id);
        return Response.ok().entity("{\"count\": " + count + "}").build();
    }
}
