package upeu.edu.pe.people.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.people.application.dto.CreateTipoAutoridadDTO;
import upeu.edu.pe.people.application.dto.TipoAutoridadDTO;
import upeu.edu.pe.people.application.dto.UpdateTipoAutoridadDTO;
import upeu.edu.pe.people.domain.services.TipoAutoridadService;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

@Path("/api/v1/tipos-autoridad")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Tipos de Autoridad", description = "Gestión de tipos de autoridades académicas")
public class TipoAutoridadController {

    @Inject
    TipoAutoridadService tipoAutoridadService;

    @GET
    @Path("/universidad/{universidadId}")
    public Response findByUniversidadId(@PathParam("universidadId") Long universidadId) {
        List<TipoAutoridadDTO> tipos = tipoAutoridadService.findByUniversidadId(universidadId);
        return Response.ok(ApiResponse.success("Tipos de autoridad obtenidos correctamente", tipos)).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        TipoAutoridadDTO tipo = tipoAutoridadService.findById(id);
        return Response.ok(ApiResponse.success("Tipo de autoridad obtenido correctamente", tipo)).build();
    }

    @GET
    @Path("/universidad/{universidadId}/maxima-autoridad")
    public Response findMaximaAutoridad(@PathParam("universidadId") Long universidadId) {
        TipoAutoridadDTO tipo = tipoAutoridadService.findMaximaAutoridad(universidadId);
        return Response.ok(ApiResponse.success("Máxima autoridad obtenida correctamente", tipo)).build();
    }

    @POST
    public Response create(@Valid CreateTipoAutoridadDTO dto) {
        TipoAutoridadDTO creado = tipoAutoridadService.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Tipo de autoridad creado correctamente", creado))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid UpdateTipoAutoridadDTO dto) {
        TipoAutoridadDTO actualizado = tipoAutoridadService.update(id, dto);
        return Response.ok(ApiResponse.success("Tipo de autoridad actualizado correctamente", actualizado)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        tipoAutoridadService.delete(id);
        return Response.ok(ApiResponse.success("Tipo de autoridad eliminado correctamente")).build();
    }
}
