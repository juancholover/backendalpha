package upeu.edu.pe.academic.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import upeu.edu.pe.academic.application.dto.EvaluacionCriterioRequestDTO;
import upeu.edu.pe.academic.application.dto.EvaluacionCriterioResponseDTO;
import upeu.edu.pe.academic.domain.services.EvaluacionCriterioService;

import java.math.BigDecimal;
import java.util.List;

@Path("/api/v1/evaluacion-criterios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EvaluacionCriterioController {

    @Inject
    EvaluacionCriterioService criterioService;

    @GET
    @Path("/seccion/{seccionId}")
    public Response findBySeccion(@PathParam("seccionId") Long seccionId) {
        List<EvaluacionCriterioResponseDTO> criterios = criterioService.findBySeccion(seccionId);
        return Response.ok(criterios).build();
    }

    @GET
    @Path("/seccion/{seccionId}/activos")
    public Response findActivosBySeccion(@PathParam("seccionId") Long seccionId) {
        List<EvaluacionCriterioResponseDTO> criterios = criterioService.findActivosBySeccion(seccionId);
        return Response.ok(criterios).build();
    }

    @GET
    @Path("/seccion/{seccionId}/tipo/{tipo}")
    public Response findByTipoAndSeccion(
            @PathParam("tipo") String tipo,
            @PathParam("seccionId") Long seccionId) {
        List<EvaluacionCriterioResponseDTO> criterios = criterioService.findByTipoAndSeccion(tipo, seccionId);
        return Response.ok(criterios).build();
    }

    @GET
    @Path("/seccion/{seccionId}/recuperables")
    public Response findRecuperablesBySeccion(@PathParam("seccionId") Long seccionId) {
        List<EvaluacionCriterioResponseDTO> criterios = criterioService.findRecuperablesBySeccion(seccionId);
        return Response.ok(criterios).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        EvaluacionCriterioResponseDTO criterio = criterioService.findById(id);
        return Response.ok(criterio).build();
    }

    @GET
    @Path("/seccion/{seccionId}/peso-total")
    public Response sumPesoBySeccion(@PathParam("seccionId") Long seccionId) {
        BigDecimal pesoTotal = criterioService.sumPesoBySeccion(seccionId);
        return Response.ok().entity("{\"pesoTotal\": " + pesoTotal + "}").build();
    }

    @GET
    @Path("/seccion/{seccionId}/peso-valido")
    public Response isPesoTotalValido(@PathParam("seccionId") Long seccionId) {
        boolean valido = criterioService.isPesoTotalValido(seccionId);
        return Response.ok().entity("{\"valido\": " + valido + "}").build();
    }

    @GET
    @Path("/seccion/{seccionId}/count")
    public Response countBySeccion(@PathParam("seccionId") Long seccionId) {
        long count = criterioService.countBySeccion(seccionId);
        return Response.ok().entity("{\"count\": " + count + "}").build();
    }

    @POST
    public Response create(@Valid EvaluacionCriterioRequestDTO requestDTO) {
        EvaluacionCriterioResponseDTO criterio = criterioService.create(requestDTO);
        return Response.status(Response.Status.CREATED).entity(criterio).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid EvaluacionCriterioRequestDTO requestDTO) {
        EvaluacionCriterioResponseDTO criterio = criterioService.update(id, requestDTO);
        return Response.ok(criterio).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        criterioService.delete(id);
        return Response.noContent().build();
    }
}
