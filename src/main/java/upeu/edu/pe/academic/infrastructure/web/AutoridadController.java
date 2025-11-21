package upeu.edu.pe.academic.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import upeu.edu.pe.academic.application.dto.AutoridadDTO;
import upeu.edu.pe.academic.application.dto.CreateAutoridadDTO;
import upeu.edu.pe.academic.application.dto.UpdateAutoridadDTO;
import upeu.edu.pe.academic.domain.services.AutoridadService;
import upeu.edu.pe.shared.response.ApiResponse;

import java.time.LocalDate;
import java.util.List;

@Path("/api/v1/autoridades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AutoridadController {

    @Inject
    AutoridadService autoridadService;

    @GET
    @Path("/universidad/{universidadId}/activas")
    public Response findActivasByUniversidadId(@PathParam("universidadId") Long universidadId) {
        List<AutoridadDTO> autoridades = autoridadService.findActivasByUniversidadId(universidadId);
        return Response.ok(ApiResponse.success("Autoridades activas obtenidas correctamente", autoridades)).build();
    }

    @GET
    @Path("/universidad/{universidadId}/vigentes")
    public Response findVigentesByUniversidadId(@PathParam("universidadId") Long universidadId) {
        List<AutoridadDTO> autoridades = autoridadService.findVigentesByUniversidadId(universidadId);
        return Response.ok(ApiResponse.success("Autoridades vigentes obtenidas correctamente", autoridades)).build();
    }

    @GET
    @Path("/universidad/{universidadId}")
    public Response findByUniversidadId(@PathParam("universidadId") Long universidadId) {
        List<AutoridadDTO> autoridades = autoridadService.findByUniversidadId(universidadId);
        return Response.ok(ApiResponse.success("Autoridades obtenidas correctamente", autoridades)).build();
    }

    @GET
    @Path("/persona/{personaId}")
    public Response findByPersonaId(@PathParam("personaId") Long personaId) {
        List<AutoridadDTO> autoridades = autoridadService.findByPersonaId(personaId);
        return Response.ok(ApiResponse.success("Historial de autoridades obtenido correctamente", autoridades)).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        AutoridadDTO autoridad = autoridadService.findById(id);
        return Response.ok(ApiResponse.success("Autoridad obtenida correctamente", autoridad)).build();
    }

    @POST
    public Response create(@Valid CreateAutoridadDTO dto) {
        AutoridadDTO creada = autoridadService.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Autoridad creada correctamente", creada))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid UpdateAutoridadDTO dto) {
        AutoridadDTO actualizada = autoridadService.update(id, dto);
        return Response.ok(ApiResponse.success("Autoridad actualizada correctamente", actualizada)).build();
    }

    @PUT
    @Path("/{id}/finalizar")
    public Response finalizar(
            @PathParam("id") Long id,
            @QueryParam("fechaFin") String fechaFinStr) {
        
        LocalDate fechaFin = fechaFinStr != null ? LocalDate.parse(fechaFinStr) : LocalDate.now();
        AutoridadDTO finalizada = autoridadService.finalizarAutoridad(id, fechaFin);
        
        return Response.ok(ApiResponse.success("Autoridad finalizada correctamente", finalizada)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        autoridadService.delete(id);
        return Response.ok(ApiResponse.success("Autoridad eliminada correctamente")).build();
    }
}
