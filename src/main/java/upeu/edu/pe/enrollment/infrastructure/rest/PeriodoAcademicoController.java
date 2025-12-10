package upeu.edu.pe.enrollment.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.enrollment.application.dto.PeriodoAcademicoRequestDTO;
import upeu.edu.pe.enrollment.application.dto.PeriodoAcademicoResponseDTO;
import upeu.edu.pe.enrollment.domain.services.PeriodoAcademicoService;

import java.util.List;

@Path("/api/v1/periodos-academicos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Períodos Académicos", description = "Gestión de semestres y períodos académicos")
public class PeriodoAcademicoController {

    @Inject
    PeriodoAcademicoService periodoService;

    @GET
    @Path("/universidad/{universidadId}")
    public Response findByUniversidad(@PathParam("universidadId") Long universidadId) {
        List<PeriodoAcademicoResponseDTO> periodos = periodoService.findByUniversidad(universidadId);
        return Response.ok(periodos).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        PeriodoAcademicoResponseDTO periodo = periodoService.findById(id);
        return Response.ok(periodo).build();
    }

    @GET
    @Path("/universidad/{universidadId}/actual")
    public Response findActualByUniversidad(@PathParam("universidadId") Long universidadId) {
        PeriodoAcademicoResponseDTO periodo = periodoService.findActualByUniversidad(universidadId);
        return Response.ok(periodo).build();
    }

    @GET
    @Path("/codigo/{codigo}/universidad/{universidadId}")
    public Response findByCodigoAndUniversidad(
            @PathParam("codigo") String codigo,
            @PathParam("universidadId") Long universidadId) {
        PeriodoAcademicoResponseDTO periodo = periodoService.findByCodigoAndUniversidad(codigo, universidadId);
        return Response.ok(periodo).build();
    }

    @GET
    @Path("/anio/{anio}/universidad/{universidadId}")
    public Response findByAnioAndUniversidad(
            @PathParam("anio") Integer anio,
            @PathParam("universidadId") Long universidadId) {
        List<PeriodoAcademicoResponseDTO> periodos = periodoService.findByAnioAndUniversidad(anio, universidadId);
        return Response.ok(periodos).build();
    }

    @GET
    @Path("/estado/{estado}/universidad/{universidadId}")
    public Response findByEstadoAndUniversidad(
            @PathParam("estado") String estado,
            @PathParam("universidadId") Long universidadId) {
        List<PeriodoAcademicoResponseDTO> periodos = periodoService.findByEstadoAndUniversidad(estado, universidadId);
        return Response.ok(periodos).build();
    }

    @GET
    @Path("/universidad/{universidadId}/activos")
    public Response findActivosAndUniversidad(@PathParam("universidadId") Long universidadId) {
        List<PeriodoAcademicoResponseDTO> periodos = periodoService.findActivosAndUniversidad(universidadId);
        return Response.ok(periodos).build();
    }

    @POST
    public Response create(@Valid PeriodoAcademicoRequestDTO requestDTO) {
        PeriodoAcademicoResponseDTO periodo = periodoService.create(requestDTO);
        return Response.status(Response.Status.CREATED).entity(periodo).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid PeriodoAcademicoRequestDTO requestDTO) {
        PeriodoAcademicoResponseDTO periodo = periodoService.update(id, requestDTO);
        return Response.ok(periodo).build();
    }

    @PUT
    @Path("/{id}/marcar-actual")
    public Response marcarComoActual(@PathParam("id") Long id) {
        PeriodoAcademicoResponseDTO periodo = periodoService.marcarComoActual(id);
        return Response.ok(periodo).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        periodoService.delete(id);
        return Response.noContent().build();
    }
}
